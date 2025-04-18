package com.biblioteca.dto;

import com.biblioteca.model.enums.Cargo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FuncionarioDTO {
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", 
            message = "CPF inválido. Use o formato: XXX.XXX.XXX-XX")
    private String cpf;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Pattern(regexp = "^\\([1-9]{2}\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}$", 
            message = "Telefone inválido. Use o formato: (99) 99999-9999")
    private String telefone;

    @NotNull(message = "O cargo é obrigatório")
    private Cargo cargo;

    @NotNull(message = "A data de admissão é obrigatória")
    private LocalDate dataAdmissao;

    private boolean ativo;
} 