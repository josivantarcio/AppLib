package com.biblioteca.dto;

import lombok.Data;

@Data
public class EstatisticasDTO {
    private long totalLivros;
    private long livrosEmprestados;
    private long livrosDisponiveis;
    private long totalAlunos;
    private long alunosAtivos;
    private long emprestimosMes;
    private long emprestimosAtrasados;
    private double taxaDevolucaoNoPrazo;
} 