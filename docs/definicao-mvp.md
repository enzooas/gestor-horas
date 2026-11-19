# Definição do MVP (Anexo C)

**Solução:** Gestor de Horas Complementares

**Usuário principal:** estudante de graduação da UCSal que precisa cumprir as horas complementares.

**Resultado principal:** conseguir registrar uma solicitação corretamente (modalidade, datas, cargas, documentos),
enviá-la e saber, a qualquer momento, quantas horas já foram aproveitadas, o que falta e quais documentos pendem.

**Fluxo de ponta a ponta:**
entrada (dados da atividade + nomes dos documentos) → processamento (validações, prazo de 5 dias úteis, conferência de
documentos, regra de limite da modalidade) → resultado (parecer: deferida/indeferida/cancelada, horas aproveitadas e
resumo de progresso com a meta de 200h do curso e a regra das 2 modalidades).

**Critérios de pronto (observáveis):**
- O menu abre com dados fictícios e mostra o resumo de progresso.
- É possível criar uma solicitação nas 15 modalidades, anexar os documentos exigidos e enviá-la ao NDE.
- Arquivo com extensão inválida é recusado com mensagem clara; documento não exigido ou repetido também.
- Solicitação sem anexos no prazo de 5 dias úteis é cancelada automaticamente.
- A simulação do NDE indefere quando faltam documentos e defere (integral ou parcialmente) respeitando o teto da modalidade.
- Modalidades que atingiram o limite deixam de ser oferecidas.
- Entradas inválidas (texto no lugar de número, data inexistente, opção fora do menu) não derrubam o programa.
- `./testar.sh` termina com todos os testes passando; `./executar.sh --demo` percorre o fluxo sem erro.

**Fora do escopo:**
- Upload real de arquivos e leitura do conteúdo dos certificados.
- Persistência em banco ou arquivo; múltiplos usuários; login.
- Integração com o portal do aluno ou sistemas institucionais.
- Interface gráfica ou web.
- Tratamento de feriados no cálculo do prazo.
- Julgamento real do NDE (é simulado por regras objetivas).

**Maior risco:** o MVP refletir mal as regras da instituição (interpretação do Ato/Resolução) e não ser percebido como útil
pelo público. Mitigação: regras copiadas dos documentos oficiais e teste com alunos reais (ver `problema-validacao.md`).

**Teste principal:** (1) técnico: suíte automatizada + `--demo`; (2) de valor: observar um aluno concluindo uma solicitação
no menu sem ajuda e registrar o que o confundiu **[PREENCHER com o resultado real do teste]**.

**Limitações conhecidas:** ver seção "Limitações conhecidas" do README.
