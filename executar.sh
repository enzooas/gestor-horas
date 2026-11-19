#!/usr/bin/env bash
# Compila e executa o programa. Argumentos opcionais: --demo | --vazio
set -euo pipefail
cd "$(dirname "$0")"
./compilar.sh > /dev/null
java -Dfile.encoding=UTF-8 -cp out/main br.ucsal.horas.Main "$@"
