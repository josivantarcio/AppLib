package com.biblioteca.controller;

import com.biblioteca.dto.ReservaCreateDTO;
import com.biblioteca.dto.ReservaDTO;
import com.biblioteca.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para gerenciamento de reservas de livros")
@SecurityRequirement(name = "bearer-key")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ALUNO')")
    @Operation(summary = "Cria uma nova reserva")
    public ResponseEntity<ReservaDTO> criar(@RequestBody @Valid ReservaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.criar(dto));
    }

    @DeleteMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ALUNO')")
    @Operation(summary = "Cancela uma reserva")
    public ResponseEntity<ReservaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ALUNO')")
    @Operation(summary = "Lista as reservas ativas de um aluno")
    public ResponseEntity<List<ReservaDTO>> listarReservasAtivas(@PathVariable Long alunoId) {
        return ResponseEntity.ok(reservaService.listarReservasAtivas(alunoId));
    }

    @PostMapping("/processar-expiradas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Processa reservas expiradas")
    public ResponseEntity<Void> processarReservasExpiradas() {
        reservaService.processarReservasExpiradas();
        return ResponseEntity.noContent().build();
    }
} 