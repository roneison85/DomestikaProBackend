package com.startsoftbr.domestikapro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.startsoftbr.domestikapro.dto.DashboardResponse;
import com.startsoftbr.domestikapro.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(service.gerar());
    }
}
