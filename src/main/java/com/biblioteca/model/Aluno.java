package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Year;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "alunos")
public class Aluno extends Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A matrícula é obrigatória")
    @Column(unique = true, nullable = false)
    private String matricula;

    @NotBlank(message = "O curso é obrigatório")
    @Column(nullable = false)
    private String curso;

    @NotNull(message = "O ano de ingresso é obrigatório")
    @Column(nullable = false)
    private Year anoIngresso;
} 