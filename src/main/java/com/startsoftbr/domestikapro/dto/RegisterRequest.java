package com.startsoftbr.domestikapro.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nome;
    private String email;
    private String senha;
}
