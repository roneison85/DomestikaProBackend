package com.startsoftbr.domestikapro.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(Map.of(
                "id", 1,
                "nome", "Usuário Teste",
                "email", "teste@teste.com"
        ));
    }
}
