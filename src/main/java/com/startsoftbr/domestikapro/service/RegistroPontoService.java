package com.startsoftbr.domestikapro.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.startsoftbr.domestikapro.dto.RegistroPontoRequest;
import com.startsoftbr.domestikapro.dto.RegistroPontoResponse;
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
}
