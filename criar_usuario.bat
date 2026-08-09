@echo off
chcp 65001 > nul
mvn clean compile exec:java -Dexec.mainClass=br.com.cadastro.util.UsuarioUtil
