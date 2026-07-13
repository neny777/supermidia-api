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

REM E-mail: usado apenas na recuperacao de senha
REM (preenchido no empacotamento a partir de deploy/mail.env)
set MAIL_HOST=__MAIL_HOST__
set MAIL_PORT=__MAIL_PORT__
set MAIL_USERNAME=__MAIL_USERNAME__
set MAIL_PASSWORD=__MAIL_PASSWORD__

echo.
echo  Iniciando o Supermidia... (feche esta janela para parar o servidor)
echo  Acesse de qualquer maquina da rede: http://IP-DESTE-COMPUTADOR:8080
echo.
java -jar supermidia.jar
pause
