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

        // regras simples de fluxo
        switch (req.getTipo()) {
            case "ENTRADA":
                boolean temEntrada = hoje.stream().anyMatch(p -> p.getTipo().equals("ENTRADA"));
                if (temEntrada) throw new RuntimeException("Entrada já registrada hoje.");
                break;

            case "PAUSA":
                boolean temPausa = hoje.stream().anyMatch(p -> p.getTipo().equals("PAUSA"));
                if (temPausa) throw new RuntimeException("Pausa já registrada.");
                break;

            case "RETORNO":
                boolean temPausaAberta = hoje.stream().anyMatch(p -> p.getTipo().equals("PAUSA"));
                if (!temPausaAberta) throw new RuntimeException("Não há pausa para retornar.");
                break;

            case "SAIDA":
                boolean temSaida = hoje.stream().anyMatch(p -> p.getTipo().equals("SAIDA"));
                if (temSaida) throw new RuntimeException("Saída já registrada hoje.");
                break;
        }

        RegistroPonto ponto = new RegistroPonto();
        ponto.setUsuarioId(usuarioId);
        ponto.setFuncionarioId(req.getFuncionarioId());
        ponto.setTipo(req.getTipo());
        ponto.setDataHora(LocalDateTime.now());

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
        ).stream().map(p -> new RegistroPontoResponse(
                p.getId(),
                p.getFuncionarioId(),
                p.getTipo(),
                p.getDataHora()
        )).toList();
    }


    public List<RegistroPontoResponse> listarHoje() {
        Long usuarioId = getUsuarioId();

        return repository.findByUsuarioIdAndDataHoraBetween(
                usuarioId, inicioDoDia(), fimDoDia()
        ).stream().map(p -> new RegistroPontoResponse(
                p.getId(),
                p.getFuncionarioId(),
                p.getTipo(),
                p.getDataHora()
        )).toList();
    }
}
