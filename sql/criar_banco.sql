IF DB_ID('CadastroClientes') IS NULL CREATE DATABASE CadastroClientes;
GO
USE CadastroClientes;
GO
IF OBJECT_ID('dbo.clientes','U') IS NULL
CREATE TABLE dbo.clientes (id BIGINT IDENTITY(1,1) PRIMARY KEY,nome NVARCHAR(120) NOT NULL,idade INT NOT NULL,cidade NVARCHAR(100) NOT NULL,email NVARCHAR(160) NOT NULL UNIQUE,telefone_fixo NVARCHAR(20) NULL,telefone_celular NVARCHAR(20) NULL);
GO

IF COL_LENGTH('dbo.clientes', 'telefone_fixo') IS NULL
    ALTER TABLE dbo.clientes ADD telefone_fixo NVARCHAR(20) NULL;
GO
IF COL_LENGTH('dbo.clientes', 'telefone_celular') IS NULL
    ALTER TABLE dbo.clientes ADD telefone_celular NVARCHAR(20) NULL;
GO
