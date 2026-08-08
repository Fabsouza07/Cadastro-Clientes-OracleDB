-- Execute no SQL*Plus como SYS AS SYSDBA.
-- Exemplo: sqlplus / as sysdba

ALTER SESSION SET CONTAINER = FREEPDB1;

SET VERIFY OFF
ACCEPT senha CHAR PROMPT 'Senha para CADASTRO_CLIENTES: ' HIDE

DECLARE
  usuario_existe NUMBER;
BEGIN
  SELECT COUNT(*)
    INTO usuario_existe
    FROM dba_users
   WHERE username = 'CADASTRO_CLIENTES';

  IF usuario_existe = 0 THEN
    EXECUTE IMMEDIATE
        'CREATE USER cadastro_clientes IDENTIFIED BY "'
        || REPLACE('&senha', '"', '""')
        || '"';
  ELSE
    EXECUTE IMMEDIATE
        'ALTER USER cadastro_clientes IDENTIFIED BY "'
        || REPLACE('&senha', '"', '""')
        || '" ACCOUNT UNLOCK';
  END IF;
END;
/

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO cadastro_clientes;
ALTER USER cadastro_clientes QUOTA UNLIMITED ON USERS;

UNDEFINE senha
SET VERIFY ON
