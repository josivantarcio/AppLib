package com.biblioteca.integration;

import com.biblioteca.config.TestContainersConfig;
import com.biblioteca.dto.AlunoCreateDTO;
import com.biblioteca.dto.EmprestimoCreateDTO;
import com.biblioteca.dto.LivroDTO;
import com.biblioteca.dto.AlunoDTO;
import com.biblioteca.model.Aluno;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AlunoRepository;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.AlunoService;
import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
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
class EmprestimoFlowIntegrationTest extends TestContainersConfig {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private LivroService livroService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    private Long alunoId;
    private Long livroId;

    @BeforeEach
    void setUp() {
        // Criar um livro
        LivroDTO livroDTO = new LivroDTO();
        livroDTO.setTitulo("Clean Code");
        livroDTO.setAutor("Robert C. Martin");
        livroDTO.setIsbn("9780132350884");
        livroDTO.setQuantidadeEstoque(2);
        livroDTO.setAnoPublicacao(2008);
        LivroDTO livroSalvo = livroService.cadastrar(livroDTO);
        livroId = livroSalvo.getId();

        // Criar um aluno
        AlunoCreateDTO alunoDTO = new AlunoCreateDTO();
        alunoDTO.setNome("João Silva");
        alunoDTO.setEmail("joao@email.com");
        alunoDTO.setMatricula("2024001");
        alunoDTO.setCurso("Engenharia");
        alunoDTO.setAnoIngresso(2024);
        alunoDTO.setSenha("senha123");
        AlunoDTO alunoSalvo = alunoService.cadastrar(alunoDTO);
        alunoId = alunoSalvo.getId();
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de empréstimo e devolução")
    void fluxoCompletoEmprestimoDevolucao() {
        // Realizar empréstimo
        EmprestimoCreateDTO emprestimoDTO = new EmprestimoCreateDTO();
        emprestimoDTO.setAlunoId(alunoId);
        emprestimoDTO.setLivroId(livroId);

        var emprestimo = emprestimoService.realizarEmprestimo(emprestimoDTO);
        assertNotNull(emprestimo);
        assertEquals(alunoId, emprestimo.getAlunoId());
        assertEquals(livroId, emprestimo.getLivroId());

        // Verificar se a quantidade em estoque foi reduzida
        Livro livro = livroRepository.findById(livroId).orElseThrow();
        assertEquals(1, livro.getQuantidadeEstoque());

        // Realizar devolução
        var devolucao = emprestimoService.realizarDevolucao(emprestimo.getId());
        assertNotNull(devolucao);
        assertNotNull(devolucao.getDataDevolucao());

        // Verificar se a quantidade em estoque foi restaurada
        livro = livroRepository.findById(livroId).orElseThrow();
        assertEquals(2, livro.getQuantidadeEstoque());
    }
} 