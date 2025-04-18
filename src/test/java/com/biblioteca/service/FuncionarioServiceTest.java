package com.biblioteca.service;

import com.biblioteca.dto.FuncionarioCreateDTO;
import com.biblioteca.dto.FuncionarioDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.Funcionario;
import com.biblioteca.model.Perfil;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.Cargo;
import com.biblioteca.repository.FuncionarioRepository;
import com.biblioteca.repository.PerfilRepository;
import com.biblioteca.repository.UsuarioRepository;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioCreateDTO funcionarioCreateDTO;
    private FuncionarioDTO funcionarioDTO;
    private Funcionario funcionario;
    private Perfil perfilBibliotecario;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Configurando o DTO de criação
        funcionarioCreateDTO = new FuncionarioCreateDTO();
        funcionarioCreateDTO.setNome("Maria Santos");
        funcionarioCreateDTO.setEmail("maria.santos@biblioteca.com");
        funcionarioCreateDTO.setCpf("123.456.789-00");
        funcionarioCreateDTO.setTelefone("(11) 98765-4321");
        funcionarioCreateDTO.setCargo(Cargo.BIBLIOTECARIO);
        funcionarioCreateDTO.setDataAdmissao(LocalDate.now());
        funcionarioCreateDTO.setSenha("senha123");

        // Configurando o DTO de resposta
        funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setId(1L);
        funcionarioDTO.setNome("Maria Santos");
        funcionarioDTO.setEmail("maria.santos@biblioteca.com");
        funcionarioDTO.setCpf("123.456.789-00");
        funcionarioDTO.setTelefone("(11) 98765-4321");
        funcionarioDTO.setCargo(Cargo.BIBLIOTECARIO);
        funcionarioDTO.setDataAdmissao(LocalDate.now());
        funcionarioDTO.setAtivo(true);

        // Configurando a entidade
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Maria Santos");
        funcionario.setEmail("maria.santos@biblioteca.com");
        funcionario.setCpf("123.456.789-00");
        funcionario.setTelefone("(11) 98765-4321");
        funcionario.setCargo(Cargo.BIBLIOTECARIO);
        funcionario.setDataAdmissao(LocalDate.now());
        funcionario.setAtivo(true);

        // Configurando o perfil
        perfilBibliotecario = new Perfil();
        perfilBibliotecario.setId(1L);
        perfilBibliotecario.setNome("ROLE_BIBLIOTECARIO");

        // Configurando o usuário
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("maria.santos@biblioteca.com");
        usuario.setPassword("senha_encriptada");
    }

    @Test
    @DisplayName("Deve cadastrar um funcionário com sucesso")
    void cadastrarFuncionarioComSucesso() {
        when(funcionarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(funcionarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(perfilRepository.findByNome("ROLE_" + funcionarioCreateDTO.getCargo().name()))
                .thenReturn(Optional.of(perfilBibliotecario));
        when(passwordEncoder.encode(anyString())).thenReturn("senha_encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);

        FuncionarioDTO resultado = funcionarioService.cadastrar(funcionarioCreateDTO);

        assertNotNull(resultado);
        assertEquals(funcionarioCreateDTO.getNome(), resultado.getNome());
        assertEquals(funcionarioCreateDTO.getEmail(), resultado.getEmail());
        assertEquals(funcionarioCreateDTO.getCpf(), resultado.getCpf());
        assertEquals(funcionarioCreateDTO.getCargo(), resultado.getCargo());
        assertTrue(resultado.isAtivo());
        
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar funcionário com CPF duplicado")
    void cadastrarFuncionarioComCpfDuplicado() {
        when(funcionarioRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> {
            funcionarioService.cadastrar(funcionarioCreateDTO);
        });

        verify(funcionarioRepository, never()).save(any(Funcionario.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar funcionário com email duplicado")
    void cadastrarFuncionarioComEmailDuplicado() {
        when(funcionarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(funcionarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> {
            funcionarioService.cadastrar(funcionarioCreateDTO);
        });

        verify(funcionarioRepository, never()).save(any(Funcionario.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar um funcionário com sucesso")
    void atualizarFuncionarioComSucesso() {
        when(funcionarioRepository.findById(anyLong())).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);

        FuncionarioDTO resultado = funcionarioService.atualizar(1L, funcionarioDTO);

        assertNotNull(resultado);
        assertEquals(funcionarioDTO.getNome(), resultado.getNome());
        assertEquals(funcionarioDTO.getEmail(), resultado.getEmail());
        assertEquals(funcionarioDTO.getCpf(), resultado.getCpf());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar funcionário inexistente")
    void atualizarFuncionarioInexistente() {
        when(funcionarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            funcionarioService.atualizar(1L, funcionarioDTO);
        });

        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID com sucesso")
    void buscarFuncionarioPorIdComSucesso() {
        when(funcionarioRepository.findById(anyLong())).thenReturn(Optional.of(funcionario));

        FuncionarioDTO resultado = funcionarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(funcionario.getNome(), resultado.getNome());
        assertEquals(funcionario.getEmail(), resultado.getEmail());
        assertEquals(funcionario.getCpf(), resultado.getCpf());
        assertEquals(funcionario.getCargo(), resultado.getCargo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar funcionário inexistente")
    void buscarFuncionarioInexistente() {
        when(funcionarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            funcionarioService.buscarPorId(1L);
        });
    }

    @Test
    @DisplayName("Deve listar todos os funcionários com sucesso")
    void listarTodosOsFuncionarios() {
        when(funcionarioRepository.findAll()).thenReturn(List.of(funcionario));

        List<FuncionarioDTO> resultado = funcionarioService.listarTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(funcionario.getNome(), resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve excluir funcionário com sucesso")
    void excluirFuncionarioComSucesso() {
        when(funcionarioRepository.findById(anyLong())).thenReturn(Optional.of(funcionario));
        doNothing().when(funcionarioRepository).delete(any(Funcionario.class));

        assertDoesNotThrow(() -> funcionarioService.excluir(1L));

        verify(funcionarioRepository, times(1)).delete(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir funcionário inexistente")
    void excluirFuncionarioInexistente() {
        when(funcionarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            funcionarioService.excluir(1L);
        });

        verify(funcionarioRepository, never()).delete(any(Funcionario.class));
    }
} 