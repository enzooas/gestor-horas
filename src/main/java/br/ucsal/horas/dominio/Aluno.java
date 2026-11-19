package br.ucsal.horas.dominio;

/**
 * Aluno fictício usado na demonstração. Cada curso define quantas horas de
 * atividades complementares exige ({@code horasExigidas}), sempre dentro da
 * faixa de 5% a 10% da carga total do curso (Resolução CONSUN nº 14/2016, art.
 * 3º). Ex.: curso de 3200h que exige 200h (6,25%).
 */
public record Aluno(String nome, String matricula, String curso, int cargaHorariaCurso, int horasExigidas) {

	public Aluno {
		if (nome == null || nome.isBlank() || matricula == null || matricula.isBlank()) {
			throw new IllegalArgumentException("Nome e matrícula são obrigatórios.");
		}
		if (cargaHorariaCurso <= 0) {
			throw new IllegalArgumentException("A carga horária do curso deve ser positiva.");
		}
		int minimoLegal = (int) Math.ceil(cargaHorariaCurso * 0.05);
		int maximoLegal = (int) Math.floor(cargaHorariaCurso * 0.10);
		if (horasExigidas < minimoLegal || horasExigidas > maximoLegal) {
			throw new IllegalArgumentException("As horas exigidas (" + horasExigidas + "h) devem ficar entre 5% e 10% "
					+ "da carga do curso: de " + minimoLegal + "h a " + maximoLegal + "h.");
		}
	}

	/** Máximo contabilizável: 10% da carga do curso, arredondado para baixo. */
	public int horasMaximas() {
		return (int) Math.floor(cargaHorariaCurso * 0.10);
	}

	/**
	 * Percentual da carga do curso que a meta representa (ex.: 6,25 para 200h de
	 * 3200h).
	 */
	public double percentualDaCarga() {
		return 100.0 * horasExigidas / cargaHorariaCurso;
	}
}
