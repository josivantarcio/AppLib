package com.biblioteca.controller;

import com.biblioteca.dto.EmprestimoCreateDTO;
import com.biblioteca.dto.EmprestimoDTO;
import com.biblioteca.service.EmprestimoService;
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
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "API para gerenciamento de empréstimos")
@SecurityRequirement(name = "bearer-key")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ASSISTENTE')")
    @Operation(summary = "Realiza um novo empréstimo")
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(@RequestBody @Valid EmprestimoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emprestimoService.realizarEmprestimo(dto));
    }

    @PostMapping("/{id}/devolucao")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ASSISTENTE')")
    @Operation(summary = "Registra a devolução de um empréstimo")
    public ResponseEntity<EmprestimoDTO> realizarDevolucao(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.realizarDevolucao(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ASSISTENTE')")
    @Operation(summary = "Lista todos os empréstimos")
    public ResponseEntity<List<EmprestimoDTO>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @GetMapping("/atrasados")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @Operation(summary = "Lista todos os empréstimos atrasados")
    public ResponseEntity<List<EmprestimoDTO>> listarAtrasados() {
        return ResponseEntity.ok(emprestimoService.listarEmprestimosAtrasados());
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO', 'ASSISTENTE')")
    @Operation(summary = "Lista todos os empréstimos de um aluno")
    public ResponseEntity<List<EmprestimoDTO>> listarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(emprestimoService.listarEmprestimosPorAluno(alunoId));
    }
} 