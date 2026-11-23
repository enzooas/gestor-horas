# Problema e validação (Anexos A e B)

## Anexo A — Ficha do problema

**Solução:** Gestor de Horas Complementares
**Equipe, turma e integrantes:** Turma: ESW-T1 — Integrantes: Enzo Amaral Santos, Fernando Santos Barbosa, Jessé Lessa Barbosa Machado, João Danilo Silva de Oliveira, João Gabriel Moreira Carvalho Araújo

| Campo | Preenchimento |
|---|---|
| Público | Estudantes de graduação da UCSal dos cursos de Análise e Desenvolvimento de Sistemas (ADS), Engenharia de Software e Psicologia que precisam cumprir horas complementares (amostra real de 4 respondentes). |
| Situação | Durante o acompanhamento contínuo das atividades complementares ao longo da graduação e, em alguns casos, apenas quando o estudante está próximo de se formar. |
| Problema | Os alunos não somam as horas acumuladas, não possuem um local centralizado para acompanhar o progresso, não sabem calcular o aproveitamento real respeitando o teto de cada categoria ou só percebem perto da formatura que faltam horas em uma segunda modalidade obrigatória. |
| Impacto | Falta de visibilidade sobre o saldo real de horas aproveitadas e risco de descobrir apenas na reta final do curso a pendência de horas em modalidades obrigatórias. |
| Alternativa atual | Guardar os certificados em uma pasta no computador ou celular sem somar as horas (75% / 3 respondentes) ou apenas consultar o portal da faculdade quando está perto de se formar (25% / 1 respondente). Nenhum utiliza planilha própria (0%). |
| Hipótese crítica | Alunos têm dificuldade em acompanhar o que já foi aproveitado e quais documentos cada modalidade exige. **Parcialmente confirmada:** a pesquisa real valida a dificuldade em acompanhar o progresso, calcular o teto por categoria e controlar modalidades obrigatórias, mas o questionário não avaliou especificamente dúvidas sobre os *documentos exigidos* em cada modalidade. |
| Evidência | Respostas reais coletadas via formulário online em 12/11/2026 (tempo médio de resposta de 36 segundos), consolidadas nas linhas do Anexo B. |
| Recorte | Registro e acompanhamento das solicitações + conferência de documentos + progresso da meta (ver `definicao-mvp.md`). |

Formulação recomendada: *Para estudantes da UCSal (dos cursos de ADS, Engenharia de Software e Psicologia), que enfrentam dificuldade em centralizar certificados, calcular o aproveitamento real dentro do teto de cada categoria e controlar a exigência de diferentes modalidades durante o acompanhamento de suas horas ao longo da graduação ou próximo à formatura, a consequência é o desconhecimento do progresso real acumulado e a descoberta tardia de horas pendentes. Hoje, esse público tenta resolver a situação por meio do armazenamento de certificados soltos em pastas no computador/celular sem contabilização ou apenas consultando o portal da faculdade na reta final do curso.*

## Anexo B — Registro de validação

Coleta realizada: formulário estruturado com 4 estudantes, em 12/11/2026 (duração média de 36 segundos por resposta).

| Data e perfil | Hipótese | Evidência observada | Decisão |
|---|---|---|---|
| 12/11/2026 — Estudantes da UCSal (ADS, Engenharia de Software e Psicologia; n=4, resposta agregada anônima, sem identificação individual de semestre) | Estudantes não possuem um controle estruturado e ativo das horas complementares já realizadas. | Na coleta real (n=4, tempo médio de 36s), 75% (3 alunos) afirmaram apenas guardar os certificados em pastas no computador ou celular sem somar as horas, e 25% (1 aluno) só olha o portal perto de se formar. Nenhum (0%) usa planilha própria. | **Manter** o foco na centralização dos certificados e na soma automática do progresso. |
| 12/11/2026 — Estudantes da UCSal (ADS, Engenharia de Software e Psicologia; n=4, resposta agregada anônima, sem identificação individual de semestre) | Alunos têm dificuldade em calcular o aproveitamento real e entender as regras de divisão de horas por categoria/modalidade. | As maiores dificuldades apontadas (25% / 1 voto cada) foram: não saber calcular o aproveitamento respeitando o teto da categoria; descobrir perto da formatura que faltam horas em uma segunda modalidade obrigatória; falta de lugar centralizado; e "Outra" (campo fechado no formulário, sem descrição aberta). | **Manter** as funcionalidades de cálculo automático de teto por categoria e alerta de modalidades obrigatórias. |
| 12/11/2026 — Estudantes da UCSal (ADS, Engenharia de Software e Psicologia; n=4, resposta agregada anônima, sem identificação individual de semestre) | Estudantes têm interesse real em utilizar uma ferramenta gratuita para organizar suas horas complementares. | 100% dos participantes (4 de 4 respondentes) marcaram "Com certeza" ao serem questionados se usariam uma ferramenta gratuita com essas funções. | **Manter** a proposta da solução e avançar para a construção do MVP. |

**Síntese dos aprendizados:** A pesquisa real realizada há 10 dias (12/11/2026) com 4 estudantes reais da UCSal (cursos de ADS, Engenharia de Software e Psicologia, com duração média de resposta de 36 segundos) demonstrou que o controle atual das horas complementares é passivo, baseado majoritariamente no arquivamento de certificados em pastas locais sem contagem de saldo (75%). As principais dores relatadas envolvem a falta de centralização e a dificuldade de aplicar as regras institucionais (teto máximo por categoria e obrigatoriedade de uma segunda modalidade). A proposta de uma ferramenta gratuita teve aprovação unânime (100% responderam "Com certeza"). Como limitação da coleta rápida via formulário fechado, não houve associação individual entre curso/semestre e cada resposta, nem campo aberto para detalhar a opção "Outra" dificuldade ou validar especificamente dúvidas sobre documentos exigidos.

**Mudanças realizadas no problema ou na solução:** Nenhuma alteração estrutural no escopo da solução foi necessária, pois as dores validadas na pesquisa real (falta de centralização, cálculo de teto por categoria e controle de modalidades obrigatórias) confirmam diretamente o recorte definido para o MVP. Para uma próxima rodada de testes (com o protótipo), recomenda-se observar na prática a etapa de "conferência de documentos" e investigar o motivo marcado como "Outra" dificuldade.

## Ética na validação (checklist)

- [x] Expliquei o objetivo acadêmico a cada participante.
- [x] Não coletei dados sensíveis nem nomes sem autorização.
- [x] Usei resultados agregados ou anonimizados.
- [x] Nenhuma saída de IA está apresentada aqui como se fosse feedback de usuário real. *(Todos os dados quantitativos e respostas registrados neste documento provêm exclusivamente da pesquisa real aplicada pela equipe com 4 estudantes da UCSal em 12/11/2026).*
