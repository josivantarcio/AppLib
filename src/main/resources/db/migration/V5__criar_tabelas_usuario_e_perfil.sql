CREATE TABLE perfis (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios_perfis (
    usuario_id BIGINT NOT NULL,
    perfis_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, perfis_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (perfis_id) REFERENCES perfis(id)
);

-- Inserir perfis básicos
INSERT INTO perfis (nome) VALUES 
    ('ROLE_ADMIN'),
    ('ROLE_BIBLIOTECARIO'),
    ('ROLE_ASSISTENTE'),
    ('ROLE_ALUNO');

-- Inserir um usuário admin (senha: admin123)
INSERT INTO usuarios (username, password) VALUES 
    ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a');

-- Associar perfil admin ao usuário admin
INSERT INTO usuarios_perfis (usuario_id, perfis_id) 
SELECT u.id, p.id 
FROM usuarios u, perfis p 
WHERE u.username = 'admin' AND p.nome = 'ROLE_ADMIN'; 