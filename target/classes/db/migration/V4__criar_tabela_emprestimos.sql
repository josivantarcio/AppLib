CREATE TABLE emprestimos (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL REFERENCES alunos(id),
    livro_id BIGINT NOT NULL REFERENCES livros(id),
    data_emprestimo TIMESTAMP NOT NULL,
    data_prevista_devolucao TIMESTAMP NOT NULL,
    data_devolucao TIMESTAMP,
    multa DECIMAL(10,2),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_emprestimos_aluno ON emprestimos(aluno_id);
CREATE INDEX idx_emprestimos_livro ON emprestimos(livro_id); 