package br.ucsal.horas.demo;

import br.ucsal.horas.dominio.Aluno;
import br.ucsal.horas.dominio.Modalidade;
import br.ucsal.horas.dominio.Solicitacao;
import br.ucsal.horas.dominio.SolicitacaoBuilder;
import br.ucsal.horas.dominio.TipoDocumento;
import br.ucsal.horas.servico.GestorHoras;

import java.time.LocalDate;

/**
 * Dados 100% fictícios para demonstração (sem pessoas reais, sem arquivos
 * reais). As datas são relativas a "hoje", então a demonstração funciona em
 * qualquer dia. Deixa o sistema com solicitações em quatro situações
 * diferentes.
 */
public final class DadosDemonstracao {

	private DadosDemonstracao() {
	}

	public static Aluno alunoDemo() {
		// Curso de 3200h que exige 200h de atividades complementares (valores reais do
		// grupo; matrícula fictícia)
		return new Aluno("Aluno Demonstração", "DEMO-0001", "Curso de Demonstração", 3200, 200);
	}

	public static void popular(GestorHoras gestor) {
		LocalDate hoje = gestor.hoje();

		// 1) Evento: deferida (20h)
		Solicitacao evento = gestor
				.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.EVENTOS_ACADEMICO_CIENTIFICOS)
						.descricao("Semana Acadêmica de Sistemas (evento fictício)")
						.periodo(hoje.minusDays(60), hoje.minusDays(56)).cargaTotal(20).cargaSolicitada(20));
		gestor.anexarDocumento(evento.getId(), TipoDocumento.CERTIFICADO, "certificado_semana_academica.pdf");
		gestor.enviarParaAnalise(evento.getId());
		gestor.analisar(evento.getId());

		// 2) Monitoria: deferida (60h)
		Solicitacao monitoria = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.MONITORIA)
				.descricao("Monitoria de Programação I (fictícia)").periodo(hoje.minusDays(150), hoje.minusDays(30))
				.cargaTotal(60).cargaSolicitada(60));
		gestor.anexarDocumento(monitoria.getId(), TipoDocumento.RELATORIO_ALUNO, "relatorio_monitoria.pdf");
		gestor.anexarDocumento(monitoria.getId(), TipoDocumento.RELATORIO_ORIENTADOR, "parecer_orientador.pdf");
		gestor.enviarParaAnalise(monitoria.getId());
		gestor.analisar(monitoria.getId());

		// 3) Extensão: aguardando anexos (falta o relatório do aluno)
		Solicitacao extensao = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.EXTENSAO)
				.descricao("Minicurso de Acessibilidade Digital (fictício)")
				.periodo(hoje.minusDays(20), hoje.minusDays(18)).cargaTotal(60).cargaSolicitada(60));
		gestor.anexarDocumento(extensao.getId(), TipoDocumento.CERTIFICADO, "certificado_minicurso.pdf");

		// 4) Curso de atualização: em análise
		Solicitacao curso = gestor.criarSolicitacao(new SolicitacaoBuilder().modalidade(Modalidade.CURSOS_ATUALIZACAO)
				.descricao("Curso de Git e GitHub (fictício)").periodo(hoje.minusDays(15), hoje.minusDays(10))
				.cargaTotal(30).cargaSolicitada(30));
		gestor.anexarDocumento(curso.getId(), TipoDocumento.CERTIFICADO, "certificado_git.png");
		gestor.enviarParaAnalise(curso.getId());
	}
}
