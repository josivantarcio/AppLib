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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AlunoDTO cadastrar(AlunoCreateDTO dto) {
        if (alunoRepository.existsByMatricula(dto.getMatricula())) {
            throw new RegraDeNegocioException("Matrícula já cadastrada");
        }
        if (alunoRepository.existsByEmail(dto.getEmail())) {
            throw new RegraDeNegocioException("Email já cadastrado");
        }

        Aluno aluno = new Aluno();
        BeanUtils.copyProperties(dto, aluno);
        aluno.setAnoIngresso(Year.of(dto.getAnoIngresso()));

        // Criar usuário associado
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getSenha()));
        
        // Definir perfil de aluno
        Perfil perfil = perfilRepository.findByNome("ROLE_ALUNO")
                .orElseThrow(() -> new RegraDeNegocioException("Perfil não encontrado"));
        usuario.getPerfis().add(perfil);
        
        usuarioRepository.save(usuario);
        
        return converterParaDTO(alunoRepository.save(aluno));
    }

    @Transactional(readOnly = true)
    public List<AlunoDTO> listarTodos() {
        return alunoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoDTO buscarPorId(Long id) {
        return converterParaDTO(buscarAlunoPorId(id));
    }

    @Transactional
    public AlunoDTO atualizar(Long id, AlunoDTO dto) {
        Aluno aluno = buscarAlunoPorId(id);
        
        alunoRepository.findByEmail(dto.getEmail())
                .ifPresent(a -> {
                    if (!a.getId().equals(id)) {
                        throw new RegraDeNegocioException("Email já cadastrado para outro aluno");
                    }
                });

        alunoRepository.findByMatricula(dto.getMatricula())
                .ifPresent(a -> {
                    if (!a.getId().equals(id)) {
                        throw new RegraDeNegocioException("Matrícula já cadastrada para outro aluno");
                    }
                });

        BeanUtils.copyProperties(dto, aluno, "id");
        return converterParaDTO(alunoRepository.save(aluno));
    }

    @Transactional
    public void excluir(Long id) {
        Aluno aluno = buscarAlunoPorId(id);
        alunoRepository.delete(aluno);
    }

    private Aluno buscarAlunoPorId(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));
    }

    private AlunoDTO converterParaDTO(Aluno aluno) {
        AlunoDTO dto = new AlunoDTO();
        BeanUtils.copyProperties(aluno, dto);
        dto.setAnoIngresso(aluno.getAnoIngresso().getValue());
        return dto;
    }
} 