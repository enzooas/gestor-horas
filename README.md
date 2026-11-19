# Gestor de Horas Complementares

> Projeto POOALAB — Programação Orientada a Objetos Avançada (AA2/TED)
> Integrantes: **[PREENCHER: nome completo de cada integrante]** — Turma: **[PREENCHER]**

Aplicação de console em Java que ajuda o aluno a **registrar, acompanhar e organizar** as solicitações de
aproveitamento de Atividades Complementares segundo as regras da UCSal (Ato nº 147/2014 e Resolução CONSUN nº 14/2016):
15 modalidades com seus limites de horas, documentos exigidos por modalidade, prazo de 5 dias úteis para anexar,
meta exigida pelo curso (200h em um curso de 3200h, dentro da faixa legal de 5% a 10% da carga) e regra das 2 modalidades. Inclui uma simulação da análise do NDE para demonstrar o ciclo completo.

## Problema, público e proposta de valor

**Público:** estudantes de graduação da UCSal que precisam cumprir horas complementares.
**Problema:** as regras são extensas (15 modalidades, tetos diferentes, documentos diferentes, prazos), o que dificulta
saber quanto já foi aproveitado, o que falta e quais documentos anexar. **[PREENCHER: impacto observado e alternativa atual,
somente com base nas evidências de `docs/problema-validacao.md`.]**
**Proposta de valor:** mostrar em um só lugar o progresso, o que falta para a meta, os documentos exigidos e os prazos,
e impedir erros comuns de preenchimento antes do envio.

## Escopo do MVP

Fluxo principal: **criar solicitação → anexar documentos → enviar ao NDE → receber o parecer → ver o progresso.**
Detalhes e o que ficou de fora em [`docs/definicao-mvp.md`](docs/definicao-mvp.md).

## Tecnologias

- **Java 21**, sem bibliotecas de terceiros (apenas o JDK). Justificativa: o foco da disciplina é POO; sem dependências, a execução é reproduzível em qualquer máquina com JDK.
- Testes automatizados com executor próprio (anotação `@Teste` + reflexão), pois o ambiente de desenvolvimento não permitia baixar JUnit. Migrar para JUnit é simples (as classes de teste já seguem o mesmo formato).

## Pré-requisitos

- **JDK 21 ou superior** (`java -version` e `javac -version` devem mostrar 21+).
- Terminal: Linux/macOS (bash) ou Windows (cmd/PowerShell).

## Como executar

Linux/macOS:

```bash
./executar.sh            # menu interativo com dados de demonstração
./executar.sh --demo     # roteiro automático do fluxo principal (bom para ensaiar e gravar vídeo)
./executar.sh --vazio    # começa sem nenhuma solicitação
```

Windows:

```bat
executar.bat
executar.bat --demo
```

Sem scripts (qualquer sistema), a partir da raiz do projeto:

```bash
mkdir -p out/main
javac -encoding UTF-8 -d out/main $(find src/main/java -name "*.java")
java -Dfile.encoding=UTF-8 -cp out/main br.ucsal.horas.Main
```

Não há credenciais, rede, banco de dados nem arquivos de configuração. Os dados de demonstração são **fictícios** e
ficam em memória (recriados a cada execução, com datas relativas ao dia atual).

## Como executar os testes

```bash
./testar.sh        # Windows: testar.bat
```

Saída esperada: `Resultado: 40 passaram, 0 falharam.` (código de saída 0).

## Roteiro curto para reproduzir o fluxo principal (menu interativo)

1. Opção `1`: veja o resumo (80h de 200h; 2 modalidades; 1 solicitação aguardando anexos e 1 em análise).
2. Opção `3`: nova solicitação (aceite `s`, escolha `7` Voluntariado, descreva, datas passadas, carga 30 e 30).
3. Opção `4`: tente anexar `certificado.exe` e veja o erro de arquivo inelegível; depois anexe `certificado.pdf` e `relatorio.pdf`.
4. Opção `5`: envie para análise. Opção `7`: o NDE julga todas as solicitações em análise.
5. Opção `1`: veja o progresso atualizado. Opção `8`: veja o histórico de eventos.

O comando `--demo` executa uma sequência equivalente sozinho.

## Estrutura

```
src/main/java/br/ucsal/horas/
  dominio/      Modalidade, Solicitacao, SolicitacaoBuilder, Anexo, Aluno, enums
  regras/       Strategy de cálculo de horas por modalidade
  servico/      GestorHoras (fachada), AnalisadorNDE, ResumoProgresso
  repositorio/  Repository (interface + implementação em memória)
  eventos/      Observer (histórico e notificação no console)
  ui/           Menu de console, entradas, formatação do resumo
  demo/         Dados fictícios e roteiro automático
src/test/java/  Testes automatizados
docs/           Validação, definição do MVP, declaração de IA e terceiros, pitch
```

## Conteúdos de POOA aplicados (bônus)

| Conceito | Onde | Por quê |
|---|---|---|
| **Strategy** | `regras/` e `Modalidade.getRegra()` | Cada modalidade calcula as horas de um jeito (teto simples; teto + 20h por trabalho). Evita `switch` por modalidade e permite nova regra sem mexer no analisador. |
| **Builder** | `SolicitacaoBuilder` | O formulário é preenchido em etapas e todas as validações de preenchimento ficam em um lugar. |
| **Observer** | `eventos/` | Histórico e avisos no console reagem a mudanças de estado sem o serviço conhecê-los. |
| **Facade** | `GestorHoras` | A interface de console conversa com uma única classe de serviço. |
| **Repository** | `repositorio/` | Persistência abstraída: trocar memória por arquivo/BD não altera o serviço. |
| **Command (simplificado)** | `ConsoleApp` | Cada opção do menu é uma ação registrada em um mapa; incluir opção = uma linha. |
| **Enum com comportamento / máquina de estados** | `Modalidade`, `StatusSolicitacao` | Limites, documentos exigidos e transições válidas vivem no próprio tipo. |
| **Collections e Streams** | `GestorHoras.resumo()`, `modalidadesDisponiveis()` | `groupingBy` + `summingInt` para consolidar horas por modalidade e por status. |
| **Records** | `Anexo`, `Aluno`, `EventoStatus`, `ResumoProgresso` | Objetos de valor imutáveis com validação no construtor. |
| **Injeção de dependência (`Clock`)** | `GestorHoras` | Permite testar o prazo de 5 dias úteis sem esperar. |
| **Reflexão e anotações** | `testes/ExecutorTestes`, `@Teste` | Descobre e executa os testes automaticamente. |
| **SOLID** | geral | Dependência de interfaces (`RegraAproveitamento`, `RepositorioSolicitacoes`, `Entrada`, `OuvinteStatus`); classes com responsabilidade única. |

**[A equipe deve conseguir explicar cada linha desta tabela na mostra.]**

## Limitações conhecidas

- Os dados ficam **em memória**: ao fechar o programa, tudo se perde (a demonstração se recria sozinha).
- **Não há upload real**: o sistema registra apenas o nome do arquivo anexado e confere a extensão (PDF, JPG, PNG).
- Prazos consideram apenas fins de semana; **feriados não são tratados**.
- Um único aluno por execução; a análise do NDE é uma **simulação** (não substitui o julgamento real da coordenação).
- Não integra com o portal do aluno nem usa dados institucionais reais.
- Regras seguem o texto do Ato nº 147/2014 e da Resolução nº 14/2016 fornecidos à equipe; podem mudar na instituição.
- A carga do curso (3200h) e a meta (200h) são as do curso da equipe e ficam em DadosDemonstracao.alunoDemo(); para outro curso, basta alterar esses dois números (a meta é validada contra a faixa de 5% a 10%). O nome e a matrícula do aluno são fictícios.

Próximo passo mais importante: persistência em arquivo e validação com mais alunos reais.

## Fontes, licenças, terceiros e uso de IA

Veja [`docs/declaracao-ia-terceiros.md`](docs/declaracao-ia-terceiros.md). Resumo: nenhuma biblioteca de terceiros
(apenas o JDK 21); regras extraídas dos documentos institucionais citados; uso de IA declarado no Anexo E.
