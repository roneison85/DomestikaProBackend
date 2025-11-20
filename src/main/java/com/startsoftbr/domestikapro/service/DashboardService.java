package com.startsoftbr.domestikapro.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.startsoftbr.domestikapro.dto.DashboardResponse;
import com.startsoftbr.domestikapro.entity.Funcionario;
import com.startsoftbr.domestikapro.entity.RegistroPonto;
import com.startsoftbr.domestikapro.repository.FuncionarioRepository;
import com.startsoftbr.domestikapro.repository.RegistroPontoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FuncionarioRepository funcionarioRepo;
    private final RegistroPontoRepository registroRepo;
    private final RegistroPontoService pontoService;

    public DashboardResponse gerar() {

        LocalDate hoje = LocalDate.now();
        LocalDateTime ini = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(23, 59, 59);

        List<Funcionario> funcs = funcionarioRepo.findAll();
        int totalFuncionarias = funcs.size();

        List<RegistroPonto> registros = registroRepo.findByDataHoraBetween(ini, fim);

        long totalEntradas = registros.stream().filter(r -> r.getTipo().equals("ENTRADA")).count();
        long totalSaidas   = registros.stream().filter(r -> r.getTipo().equals("SAIDA")).count();
        long totalPausas   = registros.stream().filter(r -> r.getTipo().equals("PAUSA")).count();
        long totalRetornos = registros.stream().filter(r -> r.getTipo().equals("RETORNO")).count();

        long totalTrabalhado = 0;
        long totalPausa = 0;

        List<DashboardResponse.RankingItem> ranking = new ArrayList<>();

        for (Funcionario f : funcs) {
            try {
                var resumo = pontoService.resumo(f.getId());

                String tt = resumo.getTotalTrabalhado();
                long segundosTrabalhados = converterParaSegundos(tt);
                totalTrabalhado += segundosTrabalhados;

                long segundosPausa = converterParaSegundos(resumo.getTotalPausa());
                totalPausa += segundosPausa;

                ranking.add(
                    new DashboardResponse.RankingItem(
                        f.getNome(),
                        resumo.getTotalTrabalhado()
                    )
                );

            } catch (Exception e) {
                // funcionário sem registro hoje → ignora
            }
        }

        // ordenar ranking decrescente
        ranking.sort((a, b) ->
                converterParaSegundos(b.getTotalTrabalhado()) >
                converterParaSegundos(a.getTotalTrabalhado()) ? 1 : -1
        );

        return new DashboardResponse(
                hoje.toString(),
                totalFuncionarias,
                totalEntradas,
                totalSaidas,
                totalPausas,
                totalRetornos,
                formatar(totalTrabalhado),
                formatar(totalPausa),
                ranking
        );
    }


    private long converterParaSegundos(String hhmm) {
        if (hhmm == null || !hhmm.contains(":")) return 0;
        String[] h = hhmm.split(":");
        return Integer.parseInt(h[0]) * 3600 + Integer.parseInt(h[1]) * 60;
    }

    private String formatar(long segundos) {
        long h = segundos / 3600;
        long m = (segundos % 3600) / 60;
        return String.format("%02d:%02d", h, m);
    }
}
