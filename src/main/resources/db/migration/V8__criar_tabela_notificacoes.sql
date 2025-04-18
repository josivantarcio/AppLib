CREATE TABLE notificacoes (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    mensagem TEXT NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP NOT NULL,
    data_leitura TIMESTAMP,
    data_envio_email TIMESTAMP,
    email_enviado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_notificacao_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id)
);

CREATE INDEX idx_notificacoes_aluno ON notificacoes(aluno_id);
CREATE INDEX idx_notificacoes_lida ON notificacoes(lida);
CREATE INDEX idx_notificacoes_email_enviado ON notificacoes(email_enviado); 