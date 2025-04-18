package com.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AlunoDTO {
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @Pattern(regexp = "^\\([1-9]{2}\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}$", 
            message = "Telefone inválido. Use o formato: (99) 99999-9999")
    private String telefone;

    @NotBlank(message = "O curso é obrigatório")
    private String curso;

    @NotNull(message = "O ano de ingresso é obrigatório")
    private Integer anoIngresso;

    private boolean ativo;
} 