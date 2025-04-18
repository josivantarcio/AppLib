package com.biblioteca.controller;

import com.biblioteca.dto.LoginDTO;
import com.biblioteca.dto.TokenDTO;
import com.biblioteca.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Deve autenticar com sucesso")
    void autenticarComSucesso() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin@biblioteca.com");
        loginDTO.setPassword("admin123");

        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(tokenService.gerarToken(auth)).thenReturn("token-test");

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-test"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("Deve retornar erro para credenciais inválidas")
    void autenticarComCredenciaisInvalidas() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("invalid@email.com");
        loginDTO.setPassword("wrong-password");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios")
    void validarCamposObrigatorios() throws Exception {
        LoginDTO loginDTO = new LoginDTO();

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }
} 