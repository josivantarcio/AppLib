package com.biblioteca.service;

import com.biblioteca.dto.AlunoCreateDTO;
import com.biblioteca.dto.AlunoDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.Aluno;
import com.biblioteca.model.Perfil;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.AlunoRepository;
import com.biblioteca.repository.PerfilRepository;
import com.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AlunoService alunoService;

    private AlunoCreateDTO alunoCreateDTO;
    private AlunoDTO alunoDTO;
    private Aluno aluno;
    private Perfil perfilAluno;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Configurando o DTO de criação
        alunoCreateDTO = new AlunoCreateDTO();
        alunoCreateDTO.setNome("João Silva");
        alunoCreateDTO.setEmail("joao.silva@email.com");
        alunoCreateDTO.setMatricula("2024001");
        alunoCreateDTO.setTelefone("(11) 98765-4321");
        alunoCreateDTO.setCurso("Engenharia de Software");
        alunoCreateDTO.setAnoIngresso(2024);
        alunoCreateDTO.setSenha("senha123");

        // Configurando o DTO de resposta
        alunoDTO = new AlunoDTO();
        alunoDTO.setId(1L);
        alunoDTO.setNome("João Silva");
        alunoDTO.setEmail("joao.silva@email.com");
        alunoDTO.setMatricula("2024001");
        alunoDTO.setTelefone("(11) 98765-4321");
        alunoDTO.setCurso("Engenharia de Software");
        alunoDTO.setAnoIngresso(2024);
        alunoDTO.setAtivo(true);

        // Configurando a entidade
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setEmail("joao.silva@email.com");
        aluno.setMatricula("2024001");
        aluno.setTelefone("(11) 98765-4321");
        aluno.setCurso("Engenharia de Software");
        aluno.setAnoIngresso(Year.of(2024));
        aluno.setAtivo(true);

        // Configurando o perfil
        perfilAluno = new Perfil();
        perfilAluno.setId(1L);
        perfilAluno.setNome("ROLE_ALUNO");

        // Configurando o usuário
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao.silva@email.com");
        usuario.setPassword("senha_encriptada");
    }

    @Test
    @DisplayName("Deve cadastrar um aluno com sucesso")
    void cadastrarAlunoComSucesso() {
        when(alunoRepository.existsByMatricula(anyString())).thenReturn(false);
        when(alunoRepository.existsByEmail(anyString())).thenReturn(false);
        when(perfilRepository.findByNome("ROLE_ALUNO")).thenReturn(Optional.of(perfilAluno));
        when(passwordEncoder.encode(anyString())).thenReturn("senha_encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoDTO resultado = alunoService.cadastrar(alunoCreateDTO);

        assertNotNull(resultado);
        assertEquals(alunoCreateDTO.getNome(), resultado.getNome());
        assertEquals(alunoCreateDTO.getEmail(), resultado.getEmail());
        assertEquals(alunoCreateDTO.getMatricula(), resultado.getMatricula());
        assertTrue(resultado.isAtivo());
        
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar aluno com matrícula duplicada")
    void cadastrarAlunoComMatriculaDuplicada() {
        when(alunoRepository.existsByMatricula(anyString())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> {
            alunoService.cadastrar(alunoCreateDTO);
        });

        verify(alunoRepository, never()).save(any(Aluno.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar aluno com email duplicado")
    void cadastrarAlunoComEmailDuplicado() {
        when(alunoRepository.existsByMatricula(anyString())).thenReturn(false);
        when(alunoRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> {
            alunoService.cadastrar(alunoCreateDTO);
        });

        verify(alunoRepository, never()).save(any(Aluno.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar um aluno com sucesso")
    void atualizarAlunoComSucesso() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoDTO resultado = alunoService.atualizar(1L, alunoDTO);

        assertNotNull(resultado);
        assertEquals(alunoDTO.getNome(), resultado.getNome());
        assertEquals(alunoDTO.getEmail(), resultado.getEmail());
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar aluno inexistente")
    void atualizarAlunoInexistente() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            alunoService.atualizar(1L, alunoDTO);
        });

        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso")
    void buscarAlunoPorIdComSucesso() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.of(aluno));

        AlunoDTO resultado = alunoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(aluno.getNome(), resultado.getNome());
        assertEquals(aluno.getEmail(), resultado.getEmail());
        assertEquals(aluno.getMatricula(), resultado.getMatricula());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno inexistente")
    void buscarAlunoInexistente() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            alunoService.buscarPorId(1L);
        });
    }

    @Test
    @DisplayName("Deve listar todos os alunos com sucesso")
    void listarTodosOsAlunos() {
        when(alunoRepository.findAll()).thenReturn(List.of(aluno));

        List<AlunoDTO> resultado = alunoService.listarTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(aluno.getNome(), resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve excluir aluno com sucesso")
    void excluirAlunoComSucesso() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.of(aluno));
        doNothing().when(alunoRepository).delete(any(Aluno.class));

        assertDoesNotThrow(() -> alunoService.excluir(1L));

        verify(alunoRepository, times(1)).delete(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir aluno inexistente")
    void excluirAlunoInexistente() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            alunoService.excluir(1L);
        });

        verify(alunoRepository, never()).delete(any(Aluno.class));
    }
} 