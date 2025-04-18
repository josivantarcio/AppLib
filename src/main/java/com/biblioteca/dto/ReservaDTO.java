package com.biblioteca.dto;

import com.biblioteca.model.StatusReserva;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaDTO {
    private Long id;
    private Long alunoId;
    private String nomeAluno;
    private Long livroId;
    private String tituloLivro;
    private LocalDateTime dataReserva;
    private LocalDateTime dataValidade;
    private StatusReserva status;
    private boolean notificado;
    private LocalDateTime dataNotificacao;
} 