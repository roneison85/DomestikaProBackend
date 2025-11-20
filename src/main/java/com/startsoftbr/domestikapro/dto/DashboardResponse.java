package com.startsoftbr.domestikapro.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {

    private String data;
    private int totalFuncionarias;

    private long totalEntradas;
    private long totalSaidas;
    private long totalPausas;
    private long totalRetornos;

    private String totalTrabalhado;
    private String totalPausa;

    private List<RankingItem> ranking;

    @Data
    @AllArgsConstructor
    public static class RankingItem {
        private String funcionaria;
        private String totalTrabalhado;
    }
}
