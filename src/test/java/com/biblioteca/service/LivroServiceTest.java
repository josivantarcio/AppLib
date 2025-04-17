package com.biblioteca.service;

import com.biblioteca.dto.LivroDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private LivroDTO livroDTO;
    private Livro livro;

    @BeforeEach
    void setUp() {
        livroDTO = new LivroDTO();
        livroDTO.setTitulo("Clean Code");
        livroDTO.setAutor("Robert C. Martin");
        livroDTO.setIsbn("9780132350884");
        livroDTO.setEditora("Prentice Hall");
        livroDTO.setAnoPublicacao(2008);
        livroDTO.setQuantidadeEstoque(5);
        livroDTO.setDisponivel(true);

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Clean Code");
        livro.setAutor("Robert C. Martin");
        livro.setIsbn("9780132350884");
        livro.setEditora("Prentice Hall");
        livro.setAnoPublicacao(Year.of(2008));
        livro.setQuantidadeEstoque(5);
        livro.setDisponivel(true);
    }

    @Test
    @DisplayName("Deve cadastrar um livro com sucesso")
    void cadastrarLivroComSucesso() {
        when(livroRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroDTO resultado = livroService.cadastrar(livroDTO);

        assertNotNull(resultado);
        assertEquals(livroDTO.getTitulo(), resultado.getTitulo());
        assertEquals(livroDTO.getIsbn(), resultado.getIsbn());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar livro com ISBN duplicado")
    void cadastrarLivroComIsbnDuplicado() {
        when(livroRepository.findByIsbn(anyString())).thenReturn(Optional.of(livro));

        assertThrows(RegraDeNegocioException.class, () -> {
            livroService.cadastrar(livroDTO);
        });

        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    void atualizarLivroComSucesso() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        when(livroRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroDTO resultado = livroService.atualizar(1L, livroDTO);

        assertNotNull(resultado);
        assertEquals(livroDTO.getTitulo(), resultado.getTitulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar livro inexistente")
    void atualizarLivroInexistente() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            livroService.atualizar(1L, livroDTO);
        });

        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve buscar livro por ID com sucesso")
    void buscarLivroPorIdComSucesso() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));

        LivroDTO resultado = livroService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(livro.getTitulo(), resultado.getTitulo());
        assertEquals(livro.getIsbn(), resultado.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar livro inexistente")
    void buscarLivroInexistente() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            livroService.buscarPorId(1L);
        });
    }

    @Test
    @DisplayName("Deve listar todos os livros com sucesso")
    void listarTodosOsLivros() {
        when(livroRepository.findAll()).thenReturn(List.of(livro));

        List<LivroDTO> resultado = livroService.listarTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(livro.getTitulo(), resultado.get(0).getTitulo());
    }

    @Test
    @DisplayName("Deve excluir livro com sucesso")
    void excluirLivroComSucesso() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        doNothing().when(livroRepository).delete(any(Livro.class));

        assertDoesNotThrow(() -> livroService.excluir(1L));

        verify(livroRepository, times(1)).delete(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir livro inexistente")
    void excluirLivroInexistente() {
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            livroService.excluir(1L);
        });

        verify(livroRepository, never()).delete(any(Livro.class));
    }
} 