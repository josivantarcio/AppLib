package com.biblioteca.security;

import com.biblioteca.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private Authentication authentication;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Configurando valores para as propriedades do TokenService
        ReflectionTestUtils.setField(tokenService, "secret", "secret-key-for-testing-with-minimum-size");
        ReflectionTestUtils.setField(tokenService, "expiration", 86400000L); // 24 horas

        // Configurando usuário e autenticação mock
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("teste@email.com");

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);
    }

    @Test
    @DisplayName("Deve gerar token com sucesso")
    void gerarToken() {
        String token = tokenService.gerarToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("Deve validar token com sucesso")
    void validarToken() {
        String token = tokenService.gerarToken(authentication);

        boolean isValido = tokenService.isTokenValido(token);

        assertTrue(isValido);
    }

    @Test
    @DisplayName("Deve invalidar token modificado")
    void invalidarTokenModificado() {
        String token = tokenService.gerarToken(authentication);
        String tokenModificado = token + "modificado";

        boolean isValido = tokenService.isTokenValido(tokenModificado);

        assertFalse(isValido);
    }

    @Test
    @DisplayName("Deve extrair ID do usuário do token")
    void extrairIdUsuario() {
        String token = tokenService.gerarToken(authentication);

        Long usuarioId = tokenService.getUsuarioId(token);

        assertEquals(usuario.getId(), usuarioId);
    }
} 