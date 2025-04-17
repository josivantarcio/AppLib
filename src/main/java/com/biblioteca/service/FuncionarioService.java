package com.biblioteca.service;

import com.biblioteca.dto.FuncionarioCreateDTO;
import com.biblioteca.dto.FuncionarioDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.Funcionario;
import com.biblioteca.model.Perfil;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.FuncionarioRepository;
import com.biblioteca.repository.PerfilRepository;
import com.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public FuncionarioDTO cadastrar(FuncionarioCreateDTO dto) {
        if (funcionarioRepository.existsByCpf(dto.getCpf())) {
            throw new RegraDeNegocioException("CPF já cadastrado");
        }
        if (funcionarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraDeNegocioException("Email já cadastrado");
        }

        Funcionario funcionario = new Funcionario();
        BeanUtils.copyProperties(dto, funcionario);

        // Criar usuário associado
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getSenha()));
        
        // Definir perfil baseado no cargo
        Perfil perfil = perfilRepository.findByNome("ROLE_" + dto.getCargo().name())
                .orElseThrow(() -> new RegraDeNegocioException("Perfil não encontrado"));
        usuario.getPerfis().add(perfil);
        
        usuarioRepository.save(usuario);
        
        return converterParaDTO(funcionarioRepository.save(funcionario));
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        return funcionarioRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO buscarPorId(Long id) {
        return converterParaDTO(buscarFuncionarioPorId(id));
    }

    @Transactional
    public FuncionarioDTO atualizar(Long id, FuncionarioDTO dto) {
        Funcionario funcionario = buscarFuncionarioPorId(id);
        
        funcionarioRepository.findByEmail(dto.getEmail())
                .ifPresent(f -> {
                    if (!f.getId().equals(id)) {
                        throw new RegraDeNegocioException("Email já cadastrado para outro funcionário");
                    }
                });

        funcionarioRepository.findByCpf(dto.getCpf())
                .ifPresent(f -> {
                    if (!f.getId().equals(id)) {
                        throw new RegraDeNegocioException("CPF já cadastrado para outro funcionário");
                    }
                });

        BeanUtils.copyProperties(dto, funcionario, "id");
        return converterParaDTO(funcionarioRepository.save(funcionario));
    }

    @Transactional
    public void excluir(Long id) {
        Funcionario funcionario = buscarFuncionarioPorId(id);
        funcionarioRepository.delete(funcionario);
    }

    private Funcionario buscarFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado"));
    }

    private FuncionarioDTO converterParaDTO(Funcionario funcionario) {
        FuncionarioDTO dto = new FuncionarioDTO();
        BeanUtils.copyProperties(funcionario, dto);
        return dto;
    }
} 