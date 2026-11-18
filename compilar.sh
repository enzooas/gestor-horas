#!/usr/bin/env bash
# Compila código principal e testes para a pasta out/. Requer JDK 21+.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out/main out/test
javac -encoding UTF-8 -d out/main $(find src/main/java -name "*.java")
javac -encoding UTF-8 -cp out/main -d out/test $(find src/test/java -name "*.java")
echo "Compilação concluída."
