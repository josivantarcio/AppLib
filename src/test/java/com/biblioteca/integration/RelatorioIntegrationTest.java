package com.biblioteca.integration;

import com.biblioteca.config.TestContainersConfig;
import com.biblioteca.dto.AlunoCreateDTO;
import com.biblioteca.dto.AlunoDTO;
import com.biblioteca.dto.EmprestimoCreateDTO;
import com.biblioteca.dto.LivroDTO;
import com.biblioteca.service.AlunoService;
import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
import com.biblioteca.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RelatorioIntegrationTest extends TestContainersConfig {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private LivroService livroService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private EmprestimoService emprestimoService;

    @BeforeEach
    void setUp() {
        // Criar livros
        LivroDTO livro1 = criarLivro("Clean Code", "Robert C. Martin", "9780132350884");
        LivroDTO livro2 = criarLivro("Domain-Driven Design", "Eric Evans", "9780321125217");

        // Criar aluno
        AlunoDTO aluno = criarAluno();

        // Realizar empréstimos
        realizarEmprestimo(aluno.getId(), livro1.getId());
        realizarEmprestimo(aluno.getId(), livro2.getId());
    }

    @Test
    @DisplayName("Deve gerar relatório de empréstimos em PDF")
    void gerarRelatorioPDF() throws Exception {
        byte[] relatorio = relatorioService.gerarRelatorioEmprestimosAtrasadosPDF();
        assertNotNull(relatorio);
        assertTrue(relatorio.length > 0);
    }

    @Test
    @DisplayName("Deve gerar relatório de livros em CSV")
    void gerarRelatorioCSV() throws Exception {
        String csv = relatorioService.gerarRelatorioLivrosMaisEmprestadosCSV();
        assertNotNull(csv);
        assertTrue(csv.contains("Clean Code"));
        assertTrue(csv.contains("Domain-Driven Design"));
    }

    private LivroDTO criarLivro(String titulo, String autor, String isbn) {
        LivroDTO livroDTO = new LivroDTO();
        livroDTO.setTitulo(titulo);
        livroDTO.setAutor(autor);
        livroDTO.setIsbn(isbn);
        livroDTO.setQuantidadeEstoque(2);
        livroDTO.setAnoPublicacao(2008);
        return livroService.cadastrar(livroDTO);
    }

    private AlunoDTO criarAluno() {
        AlunoCreateDTO alunoDTO = new AlunoCreateDTO();
        alunoDTO.setNome("João Silva");
        alunoDTO.setEmail("joao@email.com");
        alunoDTO.setMatricula("2024001");
        alunoDTO.setCurso("Engenharia");
        alunoDTO.setAnoIngresso(2024);
        alunoDTO.setSenha("senha123");
        return alunoService.cadastrar(alunoDTO);
    }

    private void realizarEmprestimo(Long alunoId, Long livroId) {
        EmprestimoCreateDTO emprestimoDTO = new EmprestimoCreateDTO();
        emprestimoDTO.setAlunoId(alunoId);
        emprestimoDTO.setLivroId(livroId);
        emprestimoService.realizarEmprestimo(emprestimoDTO);
    }
} 