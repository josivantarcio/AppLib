package com.biblioteca.integration;

import com.biblioteca.config.TestContainersConfig;
import com.biblioteca.dto.LoginDTO;
import com.biblioteca.dto.LivroDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest extends TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obterToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin@biblioteca.com");
        loginDTO.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Deve realizar CRUD completo de livro")
    void crudLivro() throws Exception {
        String token = obterToken();

        // Criar livro
        LivroDTO livroDTO = new LivroDTO();
        livroDTO.setTitulo("Clean Code");
        livroDTO.setAutor("Robert C. Martin");
        livroDTO.setIsbn("9780132350884");
        livroDTO.setQuantidadeEstoque(2);
        livroDTO.setAnoPublicacao(2008);

        MvcResult createResult = mockMvc.perform(post("/api/livros")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livroDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long livroId = objectMapper.readTree(response).get("id").asLong();

        // Buscar livro
        mockMvc.perform(get("/api/livros/" + livroId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));

        // Atualizar livro
        livroDTO.setQuantidadeEstoque(3);
        mockMvc.perform(put("/api/livros/" + livroId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livroDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque").value(3));

        // Deletar livro
        mockMvc.perform(delete("/api/livros/" + livroId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
} 