package com.biblioteca.service;

import com.biblioteca.dto.LivroDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository livroRepository;

    @Transactional
    public LivroDTO cadastrar(LivroDTO dto) {
        if (livroRepository.existsByIsbn(dto.getIsbn())) {
            throw new RegraDeNegocioException("ISBN já cadastrado");
        }

        Livro livro = new Livro();
        BeanUtils.copyProperties(dto, livro);
        livro.setAnoPublicacao(Year.of(dto.getAnoPublicacao()));
        livro.setDisponivel(dto.getQuantidadeEstoque() > 0);

        return converterParaDTO(livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public List<LivroDTO> listarTodos() {
        return livroRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LivroDTO buscarPorId(Long id) {
        return converterParaDTO(buscarLivroPorId(id));
    }

    @Transactional
    public LivroDTO atualizar(Long id, LivroDTO dto) {
        Livro livro = buscarLivroPorId(id);
        
        livroRepository.findByIsbn(dto.getIsbn())
                .ifPresent(l -> {
                    if (!l.getId().equals(id)) {
                        throw new RegraDeNegocioException("ISBN já cadastrado para outro livro");
                    }
                });

        BeanUtils.copyProperties(dto, livro, "id");
        livro.setAnoPublicacao(Year.of(dto.getAnoPublicacao()));
        livro.setDisponivel(dto.getQuantidadeEstoque() > 0);
        
        return converterParaDTO(livroRepository.save(livro));
    }

    @Transactional
    public void excluir(Long id) {
        Livro livro = buscarLivroPorId(id);
        livroRepository.delete(livro);
    }

    private Livro buscarLivroPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));
    }

    private LivroDTO converterParaDTO(Livro livro) {
        LivroDTO dto = new LivroDTO();
        BeanUtils.copyProperties(livro, dto);
        dto.setAnoPublicacao(livro.getAnoPublicacao().getValue());
        return dto;
    }
} 