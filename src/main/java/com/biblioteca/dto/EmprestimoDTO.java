package com.biblioteca.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EmprestimoDTO {
    private Long id;
    private Long alunoId;
    private String nomeAluno;
    private Long livroId;
    private String tituloLivro;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataPrevistaDevolucao;
    private LocalDateTime dataDevolucao;
    private BigDecimal multa;
    private String status;
} 