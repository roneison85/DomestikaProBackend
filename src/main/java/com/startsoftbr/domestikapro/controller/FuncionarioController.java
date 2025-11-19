package com.startsoftbr.domestikapro.controller;

import com.startsoftbr.domestikapro.service.FuncionarioService;
import com.startsoftbr.domestikapro.dto.FuncionarioRequest;
import com.startsoftbr.domestikapro.dto.FuncionarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarias")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService service;

    @PostMapping
    public ResponseEntity<FuncionarioResponse> criar(@RequestBody FuncionarioRequest req) {
        return ResponseEntity.ok(service.criar(req));
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> editar(
            @PathVariable Long id,
            @RequestBody FuncionarioRequest req) {
        return ResponseEntity.ok(service.editar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
