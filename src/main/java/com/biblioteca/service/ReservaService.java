package com.biblioteca.service;

import com.biblioteca.dto.ReservaCreateDTO;
import com.biblioteca.dto.ReservaDTO;
import com.biblioteca.exception.RecursoNaoEncontradoException;
import com.biblioteca.exception.RegraDeNegocioException;
import com.biblioteca.model.*;
import com.biblioteca.repository.AlunoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;
    private final AlunoRepository alunoRepository;
    
    private static final int DIAS_VALIDADE_RESERVA = 2;
    private static final int LIMITE_RESERVAS_ATIVAS = 3;

    @Transactional
    public ReservaDTO criar(ReservaCreateDTO dto) {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));

        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        validarNovaReserva(aluno, livro);

        Reserva reserva = new Reserva();
        reserva.setAluno(aluno);
        reserva.setLivro(livro);
        reserva.setDataReserva(LocalDateTime.now());
        reserva.setDataValidade(LocalDateTime.now().plusDays(DIAS_VALIDADE_RESERVA));
        
        if (livro.isDisponivel() && livro.getQuantidadeEstoque() > 0) {
            reserva.setStatus(StatusReserva.DISPONIVEL);
        } else {
            reserva.setStatus(StatusReserva.AGUARDANDO);
        }

        return converterParaDTO(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaDTO cancelar(Long id) {
        Reserva reserva = buscarReservaPorId(id);
        
        if (reserva.getStatus() == StatusReserva.FINALIZADA) {
            throw new RegraDeNegocioException("Não é possível cancelar uma reserva finalizada");
        }
        
        reserva.setStatus(StatusReserva.CANCELADA);
        return converterParaDTO(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarReservasAtivas(Long alunoId) {
        List<StatusReserva> statusAtivos = List.of(StatusReserva.AGUARDANDO, StatusReserva.DISPONIVEL);
        return reservaRepository.findByAlunoIdAndStatusIn(alunoId, statusAtivos)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processarReservasExpiradas() {
        List<Reserva> reservasExpiradas = reservaRepository
                .findByStatusAndDataValidadeBefore(StatusReserva.DISPONIVEL, LocalDateTime.now());
        
        for (Reserva reserva : reservasExpiradas) {
            reserva.setStatus(StatusReserva.EXPIRADA);
            reservaRepository.save(reserva);
            
            // Processa próxima reserva na fila
            processarProximaReserva(reserva.getLivro().getId());
        }
    }

    @Transactional
    public void processarProximaReserva(Long livroId) {
        List<Reserva> reservasAguardando = reservaRepository
                .findByLivroIdAndStatusOrderByDataReservaAsc(livroId, StatusReserva.AGUARDANDO);
        
        if (!reservasAguardando.isEmpty()) {
            Reserva proximaReserva = reservasAguardando.get(0);
            proximaReserva.setStatus(StatusReserva.DISPONIVEL);
            proximaReserva.setDataValidade(LocalDateTime.now().plusDays(DIAS_VALIDADE_RESERVA));
            reservaRepository.save(proximaReserva);
        }
    }

    private void validarNovaReserva(Aluno aluno, Livro livro) {
        if (!aluno.isAtivo()) {
            throw new RegraDeNegocioException("Aluno inativo não pode realizar reservas");
        }

        List<StatusReserva> statusAtivos = List.of(StatusReserva.AGUARDANDO, StatusReserva.DISPONIVEL);
        long reservasAtivas = reservaRepository.countByAlunoIdAndStatusIn(aluno.getId(), statusAtivos);
        
        if (reservasAtivas >= LIMITE_RESERVAS_ATIVAS) {
            throw new RegraDeNegocioException(
                "Aluno já atingiu o limite de " + LIMITE_RESERVAS_ATIVAS + " reservas ativas");
        }

        boolean temReservaAtiva = reservaRepository
                .findByAlunoIdAndStatusIn(aluno.getId(), statusAtivos)
                .stream()
                .anyMatch(r -> r.getLivro().getId().equals(livro.getId()));

        if (temReservaAtiva) {
            throw new RegraDeNegocioException("Aluno já possui uma reserva ativa para este livro");
        }
    }

    private Reserva buscarReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));
    }

    private ReservaDTO converterParaDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        BeanUtils.copyProperties(reserva, dto);
        dto.setAlunoId(reserva.getAluno().getId());
        dto.setNomeAluno(reserva.getAluno().getNome());
        dto.setLivroId(reserva.getLivro().getId());
        dto.setTituloLivro(reserva.getLivro().getTitulo());
        return dto;
    }
} 