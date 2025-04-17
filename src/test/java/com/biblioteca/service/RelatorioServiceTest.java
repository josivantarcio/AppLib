package com.biblioteca.service;

import com.biblioteca.model.Aluno;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
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
import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private Livro livro1;
    private Livro livro2;
    private Aluno aluno;
    private Emprestimo emprestimo1;
    private Emprestimo emprestimo2;

    @BeforeEach
    void setUp() {
        // Configurando livros
        livro1 = new Livro();
        livro1.setId(1L);
        livro1.setTitulo("Clean Code");
        livro1.setAutor("Robert C. Martin");
        livro1.setIsbn("9780132350884");
        livro1.setQuantidadeEstoque(2);
        livro1.setDisponivel(true);

        livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("Domain-Driven Design");
        livro2.setAutor("Eric Evans");
        livro2.setIsbn("9780321125217");
        livro2.setQuantidadeEstoque(1);
        livro2.setDisponivel(true);

        // Configurando aluno
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setEmail("joao@email.com");
        aluno.setMatricula("2024001");
        aluno.setAtivo(true);

        // Configurando empréstimos
        emprestimo1 = new Emprestimo();
        emprestimo1.setId(1L);
        emprestimo1.setAluno(aluno);
        emprestimo1.setLivro(livro1);
        emprestimo1.setDataEmprestimo(LocalDateTime.now().minusDays(20));
        emprestimo1.setDataPrevistaDevolucao(LocalDateTime.now().minusDays(6));

        emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);
        emprestimo2.setAluno(aluno);
        emprestimo2.setLivro(livro2);
        emprestimo2.setDataEmprestimo(LocalDateTime.now().minusDays(10));
        emprestimo2.setDataPrevistaDevolucao(LocalDateTime.now().plusDays(4));
    }

    @Test
    @DisplayName("Deve gerar relatório de empréstimos atrasados em PDF")
    void gerarRelatorioEmprestimosAtrasadosPDF() throws Exception {
        when(emprestimoRepository.findAllAtrasados(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(emprestimo1));

        byte[] relatorio = relatorioService.gerarRelatorioEmprestimosAtrasadosPDF();

        assertNotNull(relatorio);
        assertTrue(relatorio.length > 0);
    }

    @Test
    @DisplayName("Deve gerar relatório de livros mais emprestados em CSV")
    void gerarRelatorioLivrosMaisEmprestadosCSV() {
        when(livroRepository.findAll()).thenReturn(Arrays.asList(livro1, livro2));
        when(emprestimoRepository.countByLivroId(livro1.getId())).thenReturn(5L);
        when(emprestimoRepository.countByLivroId(livro2.getId())).thenReturn(3L);

        String csv = relatorioService.gerarRelatorioLivrosMaisEmprestadosCSV();

        assertNotNull(csv);
        assertTrue(csv.contains(livro1.getTitulo()));
        assertTrue(csv.contains(livro2.getTitulo()));
        assertTrue(csv.contains("5")); // Quantidade de empréstimos do livro1
        assertTrue(csv.contains("3")); // Quantidade de empréstimos do livro2
    }

    @Test
    @DisplayName("Deve gerar relatório de estoque em PDF")
    void gerarRelatorioEstoquePDF() throws Exception {
        when(livroRepository.findAll()).thenReturn(Arrays.asList(livro1, livro2));

        byte[] relatorio = relatorioService.gerarRelatorioEstoquePDF();

        assertNotNull(relatorio);
        assertTrue(relatorio.length > 0);
    }
} 