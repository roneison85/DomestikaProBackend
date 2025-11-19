package com.startsoftbr.domestikapro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class RegistroPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;      // dono da conta
    private Long funcionarioId;  // funcionária que bateu ponto

    private String tipo;         // ENTRADA, PAUSA, RETORNO, SAIDA

    private LocalDateTime dataHora;
}
