package com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    
    @NotBlank(message = "O username é obrigatório")
    private String username;
    
    @NotBlank(message = "A senha é obrigatória")
    private String password;
} 