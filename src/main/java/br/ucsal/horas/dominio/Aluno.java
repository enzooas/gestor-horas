package br.ucsal.horas.dominio;

/**
 * Aluno fictício usado na demonstração. A carga horária do curso define a meta:
 * as atividades complementares não podem ser inferiores a 5% nem superiores a 10%
 * da carga total (Resolução CONSUN nº 14/2016, art. 3º).
 */
public record Aluno(String nome, String matricula, String curso, int cargaHorariaCurso) {

    public Aluno {
        if (nome == null || nome.isBlank() || matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Nome e matrícula são obrigatórios.");
        }
        if (cargaHorariaCurso <= 0) {
            throw new IllegalArgumentException("A carga horária do curso deve ser positiva.");
        }
    }

    /** Mínimo obrigatório: 5% da carga do curso, arredondado para cima. */
    public int horasMinimas() {
        return (int) Math.ceil(cargaHorariaCurso * 0.05);
    }

    /** Máximo contabilizável: 10% da carga do curso, arredondado para baixo. */
    public int horasMaximas() {
        return (int) Math.floor(cargaHorariaCurso * 0.10);
    }
}
