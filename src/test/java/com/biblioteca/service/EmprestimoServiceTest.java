package com.biblioteca.service;

import com.biblioteca.dto.EmprestimoCreateDTO;
import com.biblioteca.dto.EmprestimoDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.Aluno;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AlunoRepository;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Aluno aluno;
    private Livro livro;
    private Emprestimo emprestimo;
    private EmprestimoCreateDTO emprestimoCreateDTO;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João");
        aluno.setAtivo(true);

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Clean Code");
        livro.setDisponivel(true);
        livro.setQuantidadeEstoque(2);

        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setAluno(aluno);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataPrevistaDevolucao(LocalDateTime.now().plusDays(14));

        emprestimoCreateDTO = new EmprestimoCreateDTO();
        emprestimoCreateDTO.setAlunoId(1L);
        emprestimoCreateDTO.setLivroId(1L);
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso")
    void realizarEmprestimoComSucesso() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        when(emprestimoRepository.countEmprestimosAtivos(anyLong())).thenReturn(0L);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoDTO resultado = emprestimoService.realizarEmprestimo(emprestimoCreateDTO);

        assertNotNull(resultado);
        assertEquals(aluno.getId(), resultado.getAlunoId());
        assertEquals(livro.getId(), resultado.getLivroId());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar emprestar para aluno inativo")
    void realizarEmprestimoAlunoInativo() {
        aluno.setAtivo(false);
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));

        assertThrows(RegraDeNegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(emprestimoCreateDTO);
        });

        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar emprestar livro indisponível")
    void realizarEmprestimoLivroIndisponivel() {
        livro.setDisponivel(false);
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));

        assertThrows(RegraDeNegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(emprestimoCreateDTO);
        });

        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve realizar devolução com sucesso")
    void realizarDevolucaoComSucesso() {
        when(emprestimoRepository.findById(anyLong())).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoDTO resultado = emprestimoService.realizarDevolucao(1L);

        assertNotNull(resultado);
        assertNotNull(resultado.getDataDevolucao());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar devolver empréstimo inexistente")
    void realizarDevolucaoEmprestimoInexistente() {
        when(emprestimoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            emprestimoService.realizarDevolucao(1L);
        });

        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }
} 