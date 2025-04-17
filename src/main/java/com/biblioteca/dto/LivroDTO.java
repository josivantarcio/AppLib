package com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LivroDTO {
    private Long id;
    
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    @NotBlank(message = "O autor é obrigatório")
    private String autor;
    
    @NotBlank(message = "O ISBN é obrigatório")
    private String isbn;
    
    private String editora;
    
    @NotNull(message = "O ano de publicação é obrigatório")
    private Integer anoPublicacao;
    
    private String genero;
    
    @PositiveOrZero(message = "A quantidade em estoque deve ser maior ou igual a zero")
    private int quantidadeEstoque;
    
    private boolean disponivel;
} 