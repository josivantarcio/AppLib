package com.biblioteca.scheduler;

import com.biblioteca.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoScheduler {

    private final NotificacaoService notificacaoService;

    @Scheduled(fixedRate = 300000) // Executa a cada 5 minutos
    public void enviarEmailsPendentes() {
        log.info("Iniciando envio de emails pendentes");
        try {
            notificacaoService.enviarEmailsPendentes();
            log.info("Envio de emails pendentes concluído com sucesso");
        } catch (Exception e) {
            log.error("Erro ao enviar emails pendentes", e);
        }
    }
} 