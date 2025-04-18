package com.biblioteca.controller;

import com.biblioteca.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "API para geração de relatórios")
@SecurityRequirement(name = "bearer-key")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/emprestimos-atrasados/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @Operation(summary = "Gera relatório de empréstimos atrasados em PDF")
    public ResponseEntity<byte[]> gerarRelatorioEmprestimosAtrasadosPDF() throws Exception {
        byte[] pdf = relatorioService.gerarRelatorioEmprestimosAtrasadosPDF();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=emprestimos-atrasados.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/livros-mais-emprestados/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @Operation(summary = "Gera relatório de livros mais emprestados em CSV")
    public ResponseEntity<String> gerarRelatorioLivrosMaisEmprestadosCSV() throws Exception {
        String csv = relatorioService.gerarRelatorioLivrosMaisEmprestadosCSV();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=livros-mais-emprestados.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/estoque/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @Operation(summary = "Gera relatório de estoque em PDF")
    public ResponseEntity<byte[]> gerarRelatorioEstoquePDF() throws Exception {
        byte[] pdf = relatorioService.gerarRelatorioEstoquePDF();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=estoque.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
} 