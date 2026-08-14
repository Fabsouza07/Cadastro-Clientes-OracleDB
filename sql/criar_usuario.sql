-- Execute no SQL*Plus como SYS AS SYSDBA.
-- Exemplo: sqlplus / as sysdba

ALTER SESSION SET CONTAINER = FREEPDB1;

SET VERIFY OFF
ACCEPT senha_owner CHAR PROMPT 'Senha para CADASTRO_CLIENTES (proprietário): ' HIDE
ACCEPT senha_app CHAR PROMPT 'Senha para CADASTRO_APP (aplicação): ' HIDE

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
        || REPLACE('&senha_owner', '"', '""')
        || '"';
  ELSE
    EXECUTE IMMEDIATE
        'ALTER USER cadastro_clientes IDENTIFIED BY "'
        || REPLACE('&senha_owner', '"', '""')
        || '" ACCOUNT UNLOCK';
  END IF;
END;
/

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO cadastro_clientes;
ALTER USER cadastro_clientes QUOTA UNLIMITED ON USERS;

DECLARE
  usuario_existe NUMBER;
BEGIN
  SELECT COUNT(*) INTO usuario_existe FROM dba_users WHERE username = 'CADASTRO_APP';
  IF usuario_existe = 0 THEN
    EXECUTE IMMEDIATE
        'CREATE USER cadastro_app IDENTIFIED BY "'
        || REPLACE('&senha_app', '"', '""')
        || '"';
  ELSE
    EXECUTE IMMEDIATE
        'ALTER USER cadastro_app IDENTIFIED BY "'
        || REPLACE('&senha_app', '"', '""')
        || '" ACCOUNT UNLOCK';
  END IF;
END;
/

GRANT CREATE SESSION TO cadastro_app;

DECLARE
BEGIN
  FOR privilegio IN (
    SELECT privilege FROM dba_sys_privs
    WHERE grantee = 'CADASTRO_APP' AND privilege <> 'CREATE SESSION'
  ) LOOP
    EXECUTE IMMEDIATE 'REVOKE ' || privilegio.privilege || ' FROM cadastro_app';
  END LOOP;

  FOR role_concedida IN (
    SELECT granted_role FROM dba_role_privs WHERE grantee = 'CADASTRO_APP'
  ) LOOP
    EXECUTE IMMEDIATE 'REVOKE ' || role_concedida.granted_role || ' FROM cadastro_app';
  END LOOP;
END;
/

UNDEFINE senha_owner
UNDEFINE senha_app
SET VERIFY ON
