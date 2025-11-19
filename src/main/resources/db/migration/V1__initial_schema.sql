CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    senha_hash VARCHAR(255)
);

CREATE TABLE funcionario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT,
    nome VARCHAR(100),
    funcao VARCHAR(100)
);

CREATE TABLE registro_ponto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    funcionario_id BIGINT,
    tipo VARCHAR(20),
    data_hora DATETIME
);
