@echo off
REM Compila e roda todos os testes automatizados.
chcp 65001 > nul
cd /d "%~dp0"
call compilar.bat > nul || exit /b 1
java -Dfile.encoding=UTF-8 -cp out\main;out\test br.ucsal.horas.testes.ExecutorTestes
