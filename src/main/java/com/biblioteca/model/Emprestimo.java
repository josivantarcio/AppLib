package com.biblioteca.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Emprestimo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Aluno aluno;
    
    @ManyToOne
    private Livro livro;
    
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataPrevistaDevolucao;
    private LocalDateTime dataDevolucao;
    private BigDecimal multa;
} 