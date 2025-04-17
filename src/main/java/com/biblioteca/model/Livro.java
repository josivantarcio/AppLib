package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.Year;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    @Column(nullable = false)
    private String autor;

    @NotBlank(message = "O ISBN é obrigatório")
    @Column(unique = true, nullable = false)
    private String isbn;

    private String editora;

    @NotNull(message = "O ano de publicação é obrigatório")
    private Year anoPublicacao;

    private String genero;

    @PositiveOrZero(message = "A quantidade em estoque deve ser maior ou igual a zero")
    private int quantidadeEstoque;

    private boolean disponivel = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
} 