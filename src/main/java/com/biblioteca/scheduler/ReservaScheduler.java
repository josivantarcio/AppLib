package com.biblioteca.scheduler;

import com.biblioteca.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaScheduler {

    private final ReservaService reservaService;

    @Scheduled(cron = "0 0 * * * *") // Executa a cada hora
    public void processarReservasExpiradas() {
        log.info("Iniciando processamento de reservas expiradas");
        try {
            reservaService.processarReservasExpiradas();
            log.info("Processamento de reservas expiradas concluído com sucesso");
        } catch (Exception e) {
            log.error("Erro ao processar reservas expiradas", e);
        }
    }

    @Scheduled(fixedRate = 3600000) // Executa a cada hora
    public void verificarReservasExpirando() {
        log.info("Iniciando verificação de reservas expirando");
        try {
            reservaService.verificarReservasExpirando();
            log.info("Verificação de reservas expirando concluída com sucesso");
        } catch (Exception e) {
            log.error("Erro ao verificar reservas expirando", e);
        }
    }
} 