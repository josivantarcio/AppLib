package com.biblioteca.model;

public enum StatusReserva {
    AGUARDANDO,      // Aguardando o livro ficar disponível
    DISPONIVEL,      // Livro disponível para retirada
    FINALIZADA,      // Reserva convertida em empréstimo
    CANCELADA,       // Reserva cancelada pelo usuário ou expirada
    EXPIRADA        // Prazo para retirada expirou
} 