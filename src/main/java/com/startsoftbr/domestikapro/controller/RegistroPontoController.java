package com.startsoftbr.domestikapro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.startsoftbr.domestikapro.dto.RegistroPontoRequest;
import com.startsoftbr.domestikapro.dto.RegistroPontoResponse;
import com.startsoftbr.domestikapro.service.RegistroPontoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ponto")
@RequiredArgsConstructor
public class RegistroPontoController {

    private final RegistroPontoService service;

    @PostMapping("/bater")
    public ResponseEntity<RegistroPontoResponse> bater(@RequestBody RegistroPontoRequest req) {
        return ResponseEntity.ok(service.bater(req));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<RegistroPontoResponse>> hoje() {
        return ResponseEntity.ok(service.listarHoje());
    }

    @GetMapping("/funcionaria/{id}")
    public ResponseEntity<List<RegistroPontoResponse>> listarPorFunc(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarHojeFuncionario(id));
    }
}
