package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    @NotNull(message = "O aluno é obrigatório")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    @NotNull(message = "O livro é obrigatório")
    private Livro livro;

    @CreatedDate
    @Column(name = "data_reserva", nullable = false, updatable = false)
    private LocalDateTime dataReserva;

    @Column(name = "data_validade", nullable = false)
    private LocalDateTime dataValidade;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusReserva status = StatusReserva.AGUARDANDO;

    @Column(name = "notificado")
    private boolean notificado = false;

    @Column(name = "data_notificacao")
    private LocalDateTime dataNotificacao;
} 