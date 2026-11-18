package br.com.pooalab.horassync.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import br.com.pooalab.horassync.model.Atividade;
import br.com.pooalab.horassync.model.CategoriaAtividade;
import br.com.pooalab.horassync.strategy.CalculoPadraoStrategy;
import br.com.pooalab.horassync.strategy.RegraCalculoStrategy;

public class GestorHorasService {
    private final List<Atividade> atividades = new ArrayList<>();
    private final double metaTotalHoras;
    private final RegraCalculoStrategy regraCalculo;

    // Construtor padrão para facilitar a chamada na UI (Meta padrão UCSal: 200h)
    public GestorHorasService() {
        this(200.0, new CalculoPadraoStrategy());
    }

    // Construtor com injeção de dependência
    public GestorHorasService(double metaTotalHoras, RegraCalculoStrategy regraCalculo) {
        this.metaTotalHoras = metaTotalHoras;
        this.regraCalculo = regraCalculo;
    }

    public void adicionarAtividade(String titulo, CategoriaAtividade categoria, double horasDeclaradas) {
        adicionarAtividade(titulo, categoria, horasDeclaradas, LocalDate.now());
    }

    public void adicionarAtividade(String titulo, CategoriaAtividade categoria, double horasDeclaradas, LocalDate dataConclusao) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Atividade nova = new Atividade(id, titulo, categoria, horasDeclaradas, dataConclusao);
        this.atividades.add(nova);
    }

    public List<Atividade> getAtividades() {
        return Collections.unmodifiableList(atividades);
    }

    public double calcularTotalHorasValidas() {
        double total = 0;
        for (CategoriaAtividade cat : CategoriaAtividade.values()) {
            total += regraCalculo.calcularHorasAproveitadas(atividades, cat);
        }
        return Math.min(total, metaTotalHoras);
    }

    public long contarModalidadesDistintas() {
        return atividades.stream()
                .map(Atividade::getCategoria)
                .distinct()
                .count();
    }

    // Regra UCSal: Mínimo de 2 modalidades diferentes
    public boolean atendeRegraMinimaModalidades() {
        return contarModalidadesDistintas() >= 2;
    }

    public boolean metaAtingida() {
        return calcularTotalHorasValidas() >= metaTotalHoras && atendeRegraMinimaModalidades();
    }

    public void carregarDadosIniciais() {
        atividades.clear();
        // Exemplo estourando teto de Eventos (Máx 60h) para demonstrar o corte no Pitch
        adicionarAtividade("Semana de Tecnologia UCSal", CategoriaAtividade.EVENTOS_ACADEMICOS, 40);
        adicionarAtividade("Congresso Brasileiro de Computação", CategoriaAtividade.EVENTOS_ACADEMICOS, 35);
        // Segunda modalidade obrigatória (Extensão - Máx 120h)
        adicionarAtividade("Curso de Extensão Java Avançado", CategoriaAtividade.EXTENSAO, 80);
        // Terceira modalidade (Monitoria - Máx 90h)
        adicionarAtividade("Monitoria de Estrutura de Dados", CategoriaAtividade.MONITORIA, 70);
    }

    public String gerarResumoRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total de Horas Válidas: %.1fh / %.1fh%n", calcularTotalHorasValidas(), metaTotalHoras));
        sb.append(String.format("Modalidades Distintas: %d (Mínimo UCSal: 2)%n", contarModalidadesDistintas()));
        sb.append("\nDetalhamento por Categoria:\n");

        for (CategoriaAtividade cat : CategoriaAtividade.values()) {
            double aproveitadas = regraCalculo.calcularHorasAproveitadas(atividades, cat);
            if (aproveitadas > 0) {
                sb.append(String.format("- %s: %.1fh (Teto: %.0fh)%n",
                        cat.getDescricao(), aproveitadas, cat.getLimiteMaximoHoras()));
            }
        }

        sb.append("\nStatus Final: ");
        if (metaAtingida()) {
            sb.append("✅ APTO PARA CONCLUIR (Meta e modalidades atingidas)");
        } else if (!atendeRegraMinimaModalidades()) {
            sb.append("⚠️ PENDENTE (Necessário cumprir ao menos 2 modalidades diferentes - Res. 14/2016)");
        } else {
            double faltam = metaTotalHoras - calcularTotalHorasValidas();
            sb.append(String.format("⚠️ PENDENTE (Faltam %.1fh válidas)", faltam));
        }
        return sb.toString();
    }
}
