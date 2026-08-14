-- Execute conectado como CADASTRO_CLIENTES, proprietário das tabelas.
-- A conta CADASTRO_APP recebe somente as permissões necessárias para a aplicação.

GRANT SELECT, INSERT, UPDATE, DELETE ON clientes TO cadastro_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON usuarios TO cadastro_app;
