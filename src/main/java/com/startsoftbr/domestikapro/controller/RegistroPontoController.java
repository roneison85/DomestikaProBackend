package com.startsoftbr.domestikapro.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.startsoftbr.domestikapro.dto.RegistroPontoRequest;
import com.startsoftbr.domestikapro.dto.RegistroPontoResponse;
import com.startsoftbr.domestikapro.dto.ResumoPontoResponse;
import com.startsoftbr.domestikapro.service.RegistroPontoService;
import com.startsoftbr.domestikapro.service.ResumoPontoPdfService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ponto")
@RequiredArgsConstructor
public class RegistroPontoController {

    private final RegistroPontoService service;
    private final ResumoPontoPdfService pdfService;

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
    
    @GetMapping("/resumo/{id}")
    public ResponseEntity<ResumoPontoResponse> resumo(@PathVariable Long id) {
        return ResponseEntity.ok(service.resumo(id));
    }
    
    @GetMapping("/resumo/{id}/pdf")
    public ResponseEntity<byte[]> resumoPdf(@PathVariable Long id) throws Exception {

        byte[] pdf = pdfService.gerar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "resumo_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }


}
