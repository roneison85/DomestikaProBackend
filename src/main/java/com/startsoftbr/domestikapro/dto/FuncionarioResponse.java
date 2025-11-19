package com.startsoftbr.domestikapro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioResponse {
    private Long id;
    private String nome;
    private String funcao;
}
