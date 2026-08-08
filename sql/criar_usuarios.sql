-- Script para criar tabela de usuários
-- Executar como administrador (SYS ou SYSTEM)

CREATE TABLE usuarios (
    id           NUMBER PRIMARY KEY,
    login        VARCHAR2(50) NOT NULL UNIQUE,
    senha        VARCHAR2(255) NOT NULL,
    nome         VARCHAR2(100) NOT NULL,
    ativo        NUMBER(1) DEFAULT 1 NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

-- Criar índice para busca por login
CREATE INDEX idx_usuarios_login ON usuarios(login);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

-- Exemplo de inserção de usuário padrão (senha: admin123)
-- A senha deve ser gerada com bcrypt cost 12
-- Use a aplicação UsuarioService.criar() para inserir novos usuários
-- INSERT INTO usuarios (login, senha, nome, ativo) 
-- VALUES ('admin', '$2y$12$...', 'Administrador', 1);

-- Commit das alterações
COMMIT;
