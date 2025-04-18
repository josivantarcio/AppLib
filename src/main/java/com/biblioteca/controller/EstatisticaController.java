package com.biblioteca.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.EstatisticasDTO;
import com.biblioteca.service.EstatisticaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
@Tag(name = "Estatísticas", description = "API para consulta de estatísticas da biblioteca")
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    @GetMapping
    @Operation(summary = "Retorna estatísticas gerais da biblioteca")
    public EstatisticasDTO getEstatisticas() {
        return estatisticaService.getEstatisticas();
    }
} 