package com.startsoftbr.domestikapro.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoPontoResponse {

    private Long funcionariaId;
    private String nome;

    private String primeiraEntrada; // HH:mm
    private String ultimaSaida;      // HH:mm

    private String totalTrabalhado;  // HH:mm
    private String totalPausa;       // HH:mm

    private List<RegistroPontoResponse> registros; // lista do dia
}
