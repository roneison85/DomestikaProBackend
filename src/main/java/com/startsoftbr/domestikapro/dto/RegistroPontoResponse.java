package com.startsoftbr.domestikapro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RegistroPontoResponse {

    private Long id;
    private Long funcionarioId;
    private String tipo;
    private LocalDateTime dataHora;
}
