package com.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AlunoCreateDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    private String email;

    @Pattern(regexp = "^\\([1-9]{2}\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}$", 
            message = "Telefone inválido. Use o formato: (99) 99999-9999")
    private String telefone;

    @NotBlank(message = "O curso é obrigatório")
    private String curso;

    @NotNull(message = "O ano de ingresso é obrigatório")
    private Integer anoIngresso;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
} 