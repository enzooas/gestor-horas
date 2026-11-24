# Anexo E — Declaração de uso de inteligência artificial e de componentes de terceiros

**Solução:** Gestor de Horas Complementares
**Turma:** ESW-T1

**Integrantes:** Enzo Amaral Santos, Fernando Santos Barbosa, Jessé Lessa Barbosa Machado, João Danilo Silva de Oliveira, João Gabriel Moreira Carvalho Araújo

## 1. Uso de inteligência artificial

Houve uso de IA? (X) Sim ( ) Não

| Ferramenta ou modelo | Finalidade ou tarefa | Parte afetada | Revisão e validação realizadas |
|---|---|---|---|
| Claude | Geração da base do código-fonte Java e dos testes automatizados, a partir dos requisitos e das regras institucionais fornecidos pela equipe | `src/main/java`, `src/test/java`, scripts de compilação, execução e testes | Enzo executou `./testar.sh` (40 testes passando) e `./executar.sh --demo`, e leu o código. Foram feitas pequenas alterações, como mudanças nos dados de demonstração e na regra da UCSal em relação às horas complementares: a faculdade exige de horas complementares 5% a 10% da carga horaria total do curso, alteramos de 160 para 200 horas (o necessário para o curso de BES). Como o curso de BES possui 3200h, 200h equivalem à 6,25% da carga, o que está dentro do limite de 5 e 10%. |
| Claude | Redação de modelos e rascunhos de documentação: README, definição do MVP, problema e validação, declaração de IA | `README.md`, `docs/` | A equipe substituiu os campos pelos dados reais da pesquisa e conferiu o texto. |

**Observações:** os requisitos do trabalho, as regras institucionais (Ato nº 147/2014 e Resolução CONSUN nº 14/2016) e as
decisões de escopo foram fornecidos pela equipe. A IA não foi usada para produzir evidências de validação com usuários:
os dados de `problema-validacao.md` vêm do formulário real aplicado pela equipe em 12/11/2026, com 4 estudantes.
Nenhum dado pessoal, credencial ou documento institucional restrito foi inserido em ferramentas externas.

## 2. Componentes e recursos de terceiros

Houve uso de componentes de terceiros? ( ) Sim (X) Não

| Componente ou recurso | Origem ou link | Versão e licença | Função e alterações realizadas |
|---|---|---|---|
| Não se aplica (apenas a biblioteca padrão do JDK 21) | — | — | — |

## 3. Materiais ou código preexistentes

Houve material produzido antes do POOALAB? (X) Sim ( ) Não

**Identificação e origem:** antes desta versão, a equipe já tinha uma implementação inicial em Java (pacote `br.ucsal.pooalab.horassync`), criada pelos próprios integrantes, com as classes de domínio (Aluno, Atividade, CategoriaAtividade), as estratégias de cálculo de horas, o serviço, uma interface de menu de console e testes unitários. Essa versão foi substituída pela base atual, e o histórico dela permanece no repositório.
**Contribuição nova realizada pela equipe:** Aplicação e análise do formulário de validação, consolidação dos gráficos, integração da interface de console, decisões de escopo e meta de 200h, preparação dos slides do pitch, gravação do vídeo de contingência, revisão e testes do projeto.

## 4. Declaração da equipe

Declaramos que as informações acima são completas e verdadeiras; que revisamos e testamos as contribuições apoiadas por IA;
que identificamos as fontes, licenças e partes preexistentes relevantes; que não apresentamos como autoria própria material
de terceiros; e que não compartilhamos dados pessoais, credenciais ou informações restritas de forma indevida.

**Responsável pela submissão:** Enzo Amaral Santos

**Ciência dos integrantes**

| Nome completo | Assinatura ou registro de ciência |
|---|---|
| Enzo Amaral Santos | Ciente em 23/11/2026 |
| Fernando Santos Barbosa | Ciente em 23/11/2026 |
| Jessé Lessa Barbosa Machado | Ciente em 23/11/2026 |
| João Danilo Silva de Oliveira | Ciente em 23/11/2026 |
| João Gabriel Moreira Carvalho Araújo | Ciente em 23/11/2026 |

Data: 23/11/2026
