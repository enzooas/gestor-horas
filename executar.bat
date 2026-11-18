@echo off
REM Compila e executa o programa. Argumentos opcionais: --demo ou --vazio
chcp 65001 > nul
cd /d "%~dp0"
call compilar.bat > nul || exit /b 1
java -Dfile.encoding=UTF-8 -cp out\main br.ucsal.horas.Main %*
