package com.biblioteca.dto;

import com.biblioteca.model.enums.Cargo;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FuncionarioDTOTest {

    @Test
    void testFuncionarioDTO() {
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(1L);
        dto.setNome("Test");
        dto.setEmail("test@test.com");
        dto.setCpf("123.456.789-00");
        dto.setTelefone("(11) 98765-4321");
        dto.setCargo(Cargo.BIBLIOTECARIO);
        dto.setDataAdmissao(LocalDate.now());
        dto.setAtivo(true);

        assertEquals(1L, dto.getId());
        assertEquals("Test", dto.getNome());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("123.456.789-00", dto.getCpf());
        assertEquals("(11) 98765-4321", dto.getTelefone());
        assertEquals(Cargo.BIBLIOTECARIO, dto.getCargo());
        assertNotNull(dto.getDataAdmissao());
        assertTrue(dto.isAtivo());
    }
} 