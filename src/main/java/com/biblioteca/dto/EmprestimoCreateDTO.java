package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmprestimoCreateDTO {
    @NotNull(message = "O ID do aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "O ID do livro é obrigatório")
    private Long livroId;
} 