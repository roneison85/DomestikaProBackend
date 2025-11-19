package com.startsoftbr.domestikapro.dto;

import lombok.Data;

@Data
public class RegistroPontoRequest {
    private Long funcionarioId;
    private String tipo; // ENTRADA, PAUSA, RETORNO, SAIDA
}
