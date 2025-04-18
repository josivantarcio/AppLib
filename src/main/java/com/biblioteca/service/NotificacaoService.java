package com.biblioteca.service;

import com.biblioteca.dto.NotificacaoDTO;
import com.biblioteca.model.*;
import com.biblioteca.repository.AlunoRepository;
import com.biblioteca.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final AlunoRepository alunoRepository;
    private final EmailService emailService;

    @Transactional
    public void criarNotificacao(Aluno aluno, TipoNotificacao tipo, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setAluno(aluno);
        notificacao.setTipo(tipo);
        notificacao.setMensagem(mensagem);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void notificarReservaDisponivel(Reserva reserva) {
        String mensagem = String.format(
            "O livro '%s' está disponível para retirada. Você tem até %s para realizar a retirada.",
            reserva.getLivro().getTitulo(),
            reserva.getDataValidade().toString()
        );
        criarNotificacao(reserva.getAluno(), TipoNotificacao.RESERVA_DISPONIVEL, mensagem);
    }

    @Transactional
    public void notificarReservaExpirando(Reserva reserva) {
        String mensagem = String.format(
            "Atenção! O prazo para retirada do livro '%s' expira em breve. Data limite: %s",
            reserva.getLivro().getTitulo(),
            reserva.getDataValidade().toString()
        );
        criarNotificacao(reserva.getAluno(), TipoNotificacao.RESERVA_EXPIRANDO, mensagem);
    }

    @Transactional
    public void notificarReservaExpirada(Reserva reserva) {
        String mensagem = String.format(
            "O prazo para retirada do livro '%s' expirou. A reserva foi cancelada.",
            reserva.getLivro().getTitulo()
        );
        criarNotificacao(reserva.getAluno(), TipoNotificacao.RESERVA_EXPIRADA, mensagem);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarNotificacoesNaoLidas(Long alunoId) {
        return notificacaoRepository.findByAlunoIdAndLidaFalseOrderByDataCriacaoDesc(alunoId)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoLida(Long id) {
        notificacaoRepository.marcarComoLida(id, LocalDateTime.now());
    }

    @Transactional
    public void enviarEmailsPendentes() {
        List<Notificacao> notificacoes = notificacaoRepository
                .findByEmailEnviadoFalseAndDataCriacaoBefore(LocalDateTime.now().minusMinutes(5));
        
        for (Notificacao notificacao : notificacoes) {
            try {
                emailService.enviarEmail(
                    notificacao.getAluno().getEmail(),
                    "Notificação da Biblioteca",
                    notificacao.getMensagem()
                );
                notificacaoRepository.marcarEmailComoEnviado(notificacao.getId(), LocalDateTime.now());
            } catch (Exception e) {
                // Log do erro e continua com as próximas notificações
            }
        }
    }

    private NotificacaoDTO converterParaDTO(Notificacao notificacao) {
        NotificacaoDTO dto = new NotificacaoDTO();
        BeanUtils.copyProperties(notificacao, dto);
        dto.setAlunoId(notificacao.getAluno().getId());
        dto.setNomeAluno(notificacao.getAluno().getNome());
        return dto;
    }
} 