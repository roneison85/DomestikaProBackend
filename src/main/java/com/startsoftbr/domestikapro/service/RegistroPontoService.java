package com.startsoftbr.domestikapro.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.startsoftbr.domestikapro.dto.RegistroPontoRequest;
import com.startsoftbr.domestikapro.dto.RegistroPontoResponse;
import com.startsoftbr.domestikapro.dto.ResumoPontoResponse;
import com.startsoftbr.domestikapro.entity.Funcionario;
import com.startsoftbr.domestikapro.entity.RegistroPonto;
import com.startsoftbr.domestikapro.entity.Usuario;
import com.startsoftbr.domestikapro.repository.FuncionarioRepository;
import com.startsoftbr.domestikapro.repository.RegistroPontoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroPontoService {

    private final RegistroPontoRepository repository;
    private final FuncionarioRepository funcionarioRepo;

    private Long getUsuarioId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario u) return u.getId();
        throw new RuntimeException("Usuário não autenticado");
    }

    private LocalDateTime inicioDoDia() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime fimDoDia() {
        return LocalDate.now().atTime(23, 59, 59);
    }


    public RegistroPontoResponse bater(RegistroPontoRequest req) {
        Long usuarioId = getUsuarioId();

        Funcionario f = funcionarioRepo.findById(req.getFuncionarioId())
                .orElseThrow(() -> new RuntimeException("Funcionária não encontrada"));

        if (!f.getUsuarioId().equals(usuarioId))
            throw new RuntimeException("Acesso negado");

        List<RegistroPonto> hoje = repository.findByFuncionarioIdAndDataHoraBetween(
                req.getFuncionarioId(), inicioDoDia(), fimDoDia()
        );

        boolean temEntrada = hoje.stream().anyMatch(p -> p.getTipo().equals("ENTRADA"));
        boolean temSaida = hoje.stream().anyMatch(p -> p.getTipo().equals("SAIDA"));

        long pausas = hoje.stream().filter(p -> p.getTipo().equals("PAUSA")).count();
        long retornos = hoje.stream().filter(p -> p.getTipo().equals("RETORNO")).count();

        RegistroPonto ponto = new RegistroPonto();
        ponto.setUsuarioId(usuarioId);
        ponto.setFuncionarioId(req.getFuncionarioId());
        ponto.setDataHora(LocalDateTime.now());
        ponto.setTipo(req.getTipo());

        switch (req.getTipo()) {

            case "ENTRADA" -> {
                if (temEntrada)
                    throw new RuntimeException("Entrada já registrada hoje.");
                if (temSaida)
                    throw new RuntimeException("Você já finalizou o expediente.");
            }

            case "PAUSA" -> {
                if (!temEntrada)
                    throw new RuntimeException("Registre a ENTRADA primeiro.");
                if (temSaida)
                    throw new RuntimeException("Já há saída registrada.");
                if (pausas > retornos)
                    throw new RuntimeException("Retorne da pausa atual primeiro.");
            }

            case "RETORNO" -> {
                if (pausas == retornos)
                    throw new RuntimeException("Nenhuma pausa em aberto.");
                if (temSaida)
                    throw new RuntimeException("Saída registrada — dia encerrado.");
            }

            case "SAIDA" -> {
                if (!temEntrada)
                    throw new RuntimeException("Registre a ENTRADA primeiro.");
                if (temSaida)
                    throw new RuntimeException("Saída já registrada hoje.");
                if (pausas > retornos)
                    throw new RuntimeException("Finalize a pausa antes da saída.");
            }
        }

        repository.save(ponto);

        return new RegistroPontoResponse(
                ponto.getId(),
                ponto.getFuncionarioId(),
                ponto.getTipo(),
                ponto.getDataHora()
        );
    }


    public List<RegistroPontoResponse> listarHojeFuncionario(Long funcionarioId) {

        Long usuarioId = getUsuarioId();

        Funcionario f = funcionarioRepo.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionária não encontrada"));

        if (!f.getUsuarioId().equals(usuarioId))
            throw new RuntimeException("Acesso negado");

        return repository.findByFuncionarioIdAndDataHoraBetween(
                funcionarioId, inicioDoDia(), fimDoDia()
        ).stream().map(p ->
                new RegistroPontoResponse(
                        p.getId(),
                        p.getFuncionarioId(),
                        p.getTipo(),
                        p.getDataHora()
                )
        ).toList();
    }


    public List<RegistroPontoResponse> listarHoje() {
        Long usuarioId = getUsuarioId();

        return repository.findByUsuarioIdAndDataHoraBetween(
                usuarioId, inicioDoDia(), fimDoDia()
        ).stream().map(p ->
                new RegistroPontoResponse(
                        p.getId(),
                        p.getFuncionarioId(),
                        p.getTipo(),
                        p.getDataHora()
                )
        ).toList();
    }
    
    public ResumoPontoResponse resumo(Long funcionarioId) {
        Long usuarioId = getUsuarioId();

        Funcionario f = funcionarioRepo.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionária não encontrada"));

        if (!f.getUsuarioId().equals(usuarioId))
            throw new RuntimeException("Acesso negado");

        List<RegistroPonto> hoje = repository.findByFuncionarioIdAndDataHoraBetween(
                funcionarioId, inicioDoDia(), fimDoDia()
        );

        hoje.sort(Comparator.comparing(RegistroPonto::getDataHora));

        if (hoje.isEmpty())
            throw new RuntimeException("Nenhum registro hoje.");

        // Conversão para DTOs
        List<RegistroPontoResponse> registrosDTO = hoje.stream()
                .map(p -> new RegistroPontoResponse(
                        p.getId(),
                        p.getFuncionarioId(),
                        p.getTipo(),
                        p.getDataHora()))
                .toList();

        LocalDateTime entrada = null;
        LocalDateTime saida = null;

        List<LocalDateTime> pausas = new ArrayList<>();
        List<LocalDateTime> retornos = new ArrayList<>();

        for (RegistroPonto r : hoje) {
            switch (r.getTipo()) {
                case "ENTRADA" -> entrada = r.getDataHora();
                case "SAIDA" -> saida = r.getDataHora();
                case "PAUSA" -> pausas.add(r.getDataHora());
                case "RETORNO" -> retornos.add(r.getDataHora());
            }
        }

        // ---- Calcular total de pausa ----
        long totalPausaSegundos = 0;
        for (int i = 0; i < Math.min(pausas.size(), retornos.size()); i++) {
            totalPausaSegundos += Duration.between(pausas.get(i), retornos.get(i)).getSeconds();
        }

        // ---- Calcular total trabalhado ----
        long totalTrabalhadoSegundos = 0;

        // períodos de trabalho
        List<LocalDateTime> inicios = new ArrayList<>();
        List<LocalDateTime> fins = new ArrayList<>();

        if (entrada != null) inicios.add(entrada);

        for (int i = 0; i < pausas.size(); i++) {
            fins.add(pausas.get(i));
            if (i < retornos.size()) inicios.add(retornos.get(i));
        }

        if (saida != null) fins.add(saida);

        for (int i = 0; i < Math.min(inicios.size(), fins.size()); i++) {
            totalTrabalhadoSegundos += Duration.between(inicios.get(i), fins.get(i)).getSeconds();
        }

        // ---- formatar HH:mm ----
        String totalPausa = formatar(totalPausaSegundos);
        String totalTrabalhado = formatar(totalTrabalhadoSegundos);

        String primeiraEntrada = entrada != null ? formatarHora(entrada) : "--:--";
        String ultimaSaida = saida != null ? formatarHora(saida) : "--:--";

        return new ResumoPontoResponse(
                funcionarioId,
                f.getNome(),
                primeiraEntrada,
                ultimaSaida,
                totalTrabalhado,
                totalPausa,
                registrosDTO
        );
    }

    private String formatar(long segundos) {
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        return String.format("%02d:%02d", horas, minutos);
    }

    private String formatarHora(LocalDateTime dt) {
        return dt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}
