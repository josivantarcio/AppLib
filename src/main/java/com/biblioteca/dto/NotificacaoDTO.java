package com.biblioteca.dto;

import com.biblioteca.model.TipoNotificacao;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacaoDTO {
    private Long id;
    private Long alunoId;
    private String nomeAluno;
    private TipoNotificacao tipo;
    private String mensagem;
    private boolean lida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataLeitura;
} 