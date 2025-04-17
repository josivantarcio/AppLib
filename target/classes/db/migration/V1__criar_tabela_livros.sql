CREATE TABLE livros (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    editora VARCHAR(255),
    ano_publicacao INTEGER NOT NULL,
    genero VARCHAR(100),
    quantidade_estoque INTEGER NOT NULL DEFAULT 0,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_livros_isbn ON livros(isbn);
CREATE INDEX idx_livros_titulo ON livros(titulo); 