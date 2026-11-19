@echo off
REM Compila codigo principal e testes para a pasta out\. Requer JDK 21+.
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out\main
mkdir out\test
dir /s /b src\main\java\*.java > out\fontes-main.txt
dir /s /b src\test\java\*.java > out\fontes-test.txt
javac -encoding UTF-8 -d out\main @out\fontes-main.txt || exit /b 1
javac -encoding UTF-8 -cp out\main -d out\test @out\fontes-test.txt || exit /b 1
echo Compilacao concluida.
