CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    livro_id BIGINT NOT NULL,
    data_reserva TIMESTAMP NOT NULL,
    data_validade TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    notificado BOOLEAN NOT NULL DEFAULT FALSE,
    data_notificacao TIMESTAMP,
    CONSTRAINT fk_reserva_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    CONSTRAINT fk_reserva_livro FOREIGN KEY (livro_id) REFERENCES livros(id)
);

CREATE INDEX idx_reservas_aluno ON reservas(aluno_id);
CREATE INDEX idx_reservas_livro ON reservas(livro_id);
CREATE INDEX idx_reservas_status ON reservas(status);
CREATE INDEX idx_reservas_data_validade ON reservas(data_validade); 