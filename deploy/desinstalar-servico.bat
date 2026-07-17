@echo off
REM Remove o servico Windows do Supermidia (execute como ADMINISTRADOR).
cd /d "%~dp0"

net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: execute este arquivo como ADMINISTRADOR.
    pause
    exit /b 1
)

supermidia-service.exe stop
supermidia-service.exe uninstall

echo.
echo  Servico "Supermidia" removido. Para rodar manualmente, use o start-supermidia.bat.
echo.
pause
