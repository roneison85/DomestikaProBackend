package com.startsoftbr.domestikapro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertaPonto {
    private String tipo;
    private String mensagem;
    private Long funcionarioId;
    private String funcionarioNome;
}
