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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final AlunoRepository alunoRepository;
    private final LivroRepository livroRepository;
    
    private static final int DIAS_EMPRESTIMO = 14;
    private static final BigDecimal MULTA_POR_DIA = new BigDecimal("1.00");
    private static final int LIMITE_EMPRESTIMOS = 3;

    @Transactional
    public EmprestimoDTO realizarEmprestimo(EmprestimoCreateDTO dto) {
        Aluno aluno = buscarAluno(dto.getAlunoId());
        Livro livro = buscarLivro(dto.getLivroId());

        validarEmprestimo(aluno, livro);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setAluno(aluno);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataPrevistaDevolucao(LocalDateTime.now().plusDays(DIAS_EMPRESTIMO));

        livro.setQuantidadeEstoque(livro.getQuantidadeEstoque() - 1);
        if (livro.getQuantidadeEstoque() == 0) {
            livro.setDisponivel(false);
        }
        
        livroRepository.save(livro);
        return converterParaDTO(emprestimoRepository.save(emprestimo));
    }

    @Transactional
    public EmprestimoDTO realizarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = buscarEmprestimo(emprestimoId);

        if (emprestimo.getDataDevolucao() != null) {
            throw new RegraDeNegocioException("Este empréstimo já foi devolvido");
        }

        emprestimo.setDataDevolucao(LocalDateTime.now());
        
        // Calcula multa se houver atraso
        if (LocalDateTime.now().isAfter(emprestimo.getDataPrevistaDevolucao())) {
            long diasAtraso = ChronoUnit.DAYS.between(
                emprestimo.getDataPrevistaDevolucao(), 
                LocalDateTime.now()
            );
            emprestimo.setMulta(MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso)));
        }

        Livro livro = emprestimo.getLivro();
        livro.setQuantidadeEstoque(livro.getQuantidadeEstoque() + 1);
        livro.setDisponivel(true);
        livroRepository.save(livro);

        return converterParaDTO(emprestimoRepository.save(emprestimo));
    }

    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarTodos() {
        return emprestimoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarEmprestimosAtrasados() {
        return emprestimoRepository.findAllAtrasados(LocalDateTime.now()).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarEmprestimosPorAluno(Long alunoId) {
        return emprestimoRepository.findByAlunoId(alunoId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private void validarEmprestimo(Aluno aluno, Livro livro) {
        if (!aluno.isAtivo()) {
            throw new RegraDeNegocioException("Aluno inativo não pode realizar empréstimos");
        }

        if (!livro.isDisponivel()) {
            throw new RegraDeNegocioException("Livro indisponível para empréstimo");
        }

        if (livro.getQuantidadeEstoque() <= 0) {
            throw new RegraDeNegocioException("Não há exemplares disponíveis para empréstimo");
        }

        long emprestimosAtivos = emprestimoRepository.countEmprestimosAtivos(aluno.getId());
        if (emprestimosAtivos >= LIMITE_EMPRESTIMOS) {
            throw new RegraDeNegocioException(
                "Aluno já atingiu o limite de " + LIMITE_EMPRESTIMOS + " empréstimos simultâneos");
        }
    }

    private Aluno buscarAluno(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));
    }

    private Livro buscarLivro(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));
    }

    private Emprestimo buscarEmprestimo(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo não encontrado"));
    }

    private EmprestimoDTO converterParaDTO(Emprestimo emprestimo) {
        EmprestimoDTO dto = new EmprestimoDTO();
        BeanUtils.copyProperties(emprestimo, dto);
        
        dto.setAlunoId(emprestimo.getAluno().getId());
        dto.setNomeAluno(emprestimo.getAluno().getNome());
        dto.setLivroId(emprestimo.getLivro().getId());
        dto.setTituloLivro(emprestimo.getLivro().getTitulo());
        
        if (emprestimo.getDataDevolucao() == null) {
            if (LocalDateTime.now().isAfter(emprestimo.getDataPrevistaDevolucao())) {
                dto.setStatus("ATRASADO");
            } else {
                dto.setStatus("EM_ANDAMENTO");
            }
        } else {
            dto.setStatus("DEVOLVIDO");
        }
        
        return dto;
    }
} 