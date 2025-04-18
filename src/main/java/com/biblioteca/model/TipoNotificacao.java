package com.biblioteca.model;

public enum TipoNotificacao {
    RESERVA_DISPONIVEL,      // Livro reservado está disponível para retirada
    RESERVA_EXPIRANDO,       // Prazo para retirada está próximo de expirar
    RESERVA_EXPIRADA,        // Prazo para retirada expirou
    RESERVA_CANCELADA,       // Reserva foi cancelada
    EMPRESTIMO_ATRASADO,     // Empréstimo está atrasado
    DEVOLUCAO_PROXIMA        // Data de devolução está próxima
} 