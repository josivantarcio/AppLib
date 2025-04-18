package com.biblioteca.controller;

import com.biblioteca.dto.NotificacaoDTO;
import com.biblioteca.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "API para gerenciamento de notificações")
@SecurityRequirement(name = "bearer-key")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ALUNO')")
    @Operation(summary = "Lista as notificações não lidas de um aluno")
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoesNaoLidas(@PathVariable Long alunoId) {
        return ResponseEntity.ok(notificacaoService.listarNotificacoesNaoLidas(alunoId));
    }

    @PutMapping("/{id}/marcar-como-lida")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ALUNO')")
    @Operation(summary = "Marca uma notificação como lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }
} 