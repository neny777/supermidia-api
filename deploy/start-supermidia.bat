@echo off
title Supermidia - Servidor
REM ============================================================
REM  Supermidia - inicia o servidor (backend + frontend juntos)
REM  Edite as variaveis abaixo se o seu MySQL usar outra senha.
REM ============================================================

set DB_HOST=localhost
set DB_PORT=3306
set DB_DATABASE_NAME=supermidia
set DB_USERNAME=root
set DB_PASSWORD=root

REM Chave de assinatura dos logins (gerada no empacotamento)
set JWT_SECRET_KEY=__JWT_SECRET__

REM E-mail: usado apenas na recuperacao de senha (pode deixar assim no teste)
set MAIL_HOST=smtp.example.com
set MAIL_PORT=465
set MAIL_USERNAME=nao-configurado
set MAIL_PASSWORD=nao-configurado

echo.
echo  Iniciando o Supermidia... (feche esta janela para parar o servidor)
echo  Acesse de qualquer maquina da rede: http://IP-DESTE-COMPUTADOR:8080
echo.
java -jar supermidia.jar
pause
