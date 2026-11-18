#!/usr/bin/env bash
# Compila e roda todos os testes automatizados. Sai com código 1 se algum falhar.
set -euo pipefail
cd "$(dirname "$0")"
./compilar.sh > /dev/null
java -Dfile.encoding=UTF-8 -cp out/main:out/test br.ucsal.horas.testes.ExecutorTestes
