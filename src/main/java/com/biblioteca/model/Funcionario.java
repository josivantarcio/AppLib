package com.biblioteca.model;

import com.biblioteca.model.enums.Cargo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "funcionarios")
public class Funcionario extends Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", 
            message = "CPF inválido. Use o formato: XXX.XXX.XXX-XX")
    @Column(unique = true, nullable = false)
    private String cpf;

    @NotNull(message = "O cargo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

    @NotNull(message = "A data de admissão é obrigatória")
    @Column(nullable = false)
    private LocalDate dataAdmissao;
} 