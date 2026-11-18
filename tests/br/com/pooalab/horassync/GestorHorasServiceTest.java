package br.com.pooalab.horassync;

import br.com.pooalab.horassync.model.Atividade;
import br.com.pooalab.horassync.model.CategoriaAtividade;
import br.com.pooalab.horassync.service.GestorHorasService;
import br.com.pooalab.horassync.strategy.CalculoPadraoStrategy;
import br.com.pooalab.horassync.strategy.RegraCalculoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestorHorasServiceTest {

    private GestorHorasService service;

    @BeforeEach
    void setUp() {
        service = new GestorHorasService(200.0, new CalculoPadraoStrategy());
    }

    @Test
    @DisplayName("Deve cortar horas excedentes respeitando o teto da categoria (Ato 147/2014 UCSal)")
    void deveLimitarHorasAoTetoDaCategoria() {
        // Eventos acadêmicos têm limite máximo de 60h na UCSal
        service.adicionarAtividade("Semana Acadêmica", CategoriaAtividade.EVENTOS_ACADEMICOS, 40.0, LocalDate.now());
        service.adicionarAtividade("Simpósio de TI", CategoriaAtividade.EVENTOS_ACADEMICOS, 40.0, LocalDate.now());

        assertEquals(60.0, service.calcularTotalHorasValidas(), 0.01);
    }

    @Test
    @DisplayName("Deve reprovar conclusão se aluno cumprir apenas 1 modalidade (Resolução 14/2016)")
    void deveExigirPeloMenosDuasModalidades() {
        GestorHorasService serviceMeta100 = new GestorHorasService(100.0, new CalculoPadraoStrategy());
        serviceMeta100.adicionarAtividade("Iniciação Científica", CategoriaAtividade.PESQUISA, 120.0);

        assertFalse(serviceMeta100.atendeRegraMinimaModalidades());
        assertFalse(serviceMeta100.metaAtingida());
    }

    @Test
    @DisplayName("Deve aprovar conclusão quando atingir meta global e mínimo de 2 modalidades")
    void deveAprovarQuandoAtenderTodasAsRegras() {
        service.carregarDadosIniciais(); // 60h Eventos + 80h Extensão + 70h Monitoria = 210h -> teto global 200h

        assertEquals(200.0, service.calcularTotalHorasValidas(), 0.01);
        assertEquals(3, service.contarModalidadesDistintas());
        assertTrue(service.atendeRegraMinimaModalidades());
        assertTrue(service.metaAtingida());
    }

    @Test
    @DisplayName("Deve delegar o cálculo corretamente para a Strategy injetada (Mock/Stub)")
    void deveDelegarCalculoParaStrategyMockada() {
        // Mock/Stub funcional da interface RegraCalculoStrategy (sem depender de JAR externo)
        RegraCalculoStrategy strategyMock = new RegraCalculoStrategy() {
            @Override
            public double calcularHorasAproveitadas(List<Atividade> atividades, CategoriaAtividade categoria) {
                return categoria == CategoriaAtividade.EXTENSAO ? 50.0 : 0.0;
            }
        };

        GestorHorasService serviceComMock = new GestorHorasService(200.0, strategyMock);
        serviceComMock.adicionarAtividade("Curso Teste", CategoriaAtividade.EXTENSAO, 10.0);

        assertEquals(50.0, serviceComMock.calcularTotalHorasValidas(), 0.01);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar atividade com carga horária inválida")
    void deveLancarExcecaoParaCargaHorariaNegativaOuZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.adicionarAtividade("Workshop Inválido", CategoriaAtividade.EXTENSAO, 0.0);
        });
    }
}
