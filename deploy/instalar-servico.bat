@echo off
REM ============================================================
REM  Instala o Supermidia como SERVICO do Windows (sem janela).
REM  Execute como ADMINISTRADOR (botao direito -> Executar como administrador).
REM ============================================================
cd /d "%~dp0"

net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: execute este arquivo como ADMINISTRADOR.
    pause
    exit /b 1
)

supermidia-service.exe install
if %errorlevel% neq 0 (
    echo ERRO ao instalar o servico. Veja a mensagem acima.
    pause
    exit /b 1
)
supermidia-service.exe start

echo.
echo  Servico "Supermidia" instalado e iniciado.
echo  Ele sobe sozinho quando o Windows liga - nao precisa de janela aberta.
echo  Logs do servidor: pasta logs\ dentro de C:\supermidia
echo  Gerenciar: services.msc (procure por "Supermidia")
echo.
pause
