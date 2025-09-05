import java.util.List;
import java.util.ArrayList;

public class Aluno extends Usuario {
    private String matriculaAluno;
    private List<Matricula> matriculas;

    public Aluno(String id, String nome, String email, String senha, String matriculaAluno) {
        super(id, nome, email, senha);
        this.matriculaAluno = matriculaAluno;
        this.matriculas = new ArrayList<>();
    }

    public Matricula matricular(Disciplina d, Curriculo c, TipoDisciplina t, Secretaria secretaria) {
        if (!secretaria.podeMatricular()) {
            throw new IllegalStateException("Fora do periodo de matricula");
        }
        if (d == null || c == null) {
            throw new IllegalArgumentException("Disciplina ou curriculo invalido");
        }
        // A disciplina precisa estar no curriculo escolhido (sem misturar cursos/semestres)
        if (!c.getDisciplinas().contains(d)) {
            throw new IllegalArgumentException("Disciplina nao faz parte do curriculo selecionado");
        }

        // Limites por CURRICULO (semestre): 4 obrigatorias + 2 optativas
        int obrigatoriasAtivas = contarMatriculasAtivas(TipoDisciplina.OBRIGATORIA, c);
        int optativasAtivas    = contarMatriculasAtivas(TipoDisciplina.OPTATIVA, c);

        if (t == TipoDisciplina.OBRIGATORIA && obrigatoriasAtivas >= 4) {
            throw new IllegalStateException("Limite de 4 disciplinas obrigatorias atingido neste curriculo");
        }
        if (t == TipoDisciplina.OPTATIVA && optativasAtivas >= 2) {
            throw new IllegalStateException("Limite de 2 disciplinas optativas atingido neste curriculo");
        }

        // Inscreve na disciplina (confere ativa/capacidade/duplicidade)
        Matricula matricula = d.inscrever(this, t);
        matricula.setCurriculo(c);
        this.matriculas.add(matricula);

        // >>> Removido: cobranca por disciplina. A cobranca agora e consolidada por semestre
        // via Secretaria.notificarCobrancaDoSemestre(...)

        return matricula;
    }

    private int contarMatriculasAtivas(TipoDisciplina tipo, Curriculo curriculo) {
        int count = 0;
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA
                && m.getTipo() == tipo
                && curriculo.equals(m.getCurriculo())) {
                count++;
            }
        }
        return count;
    }

    public void cancelar(Matricula m, Secretaria secretaria) {
        if (!secretaria.podeCancelar()) {
            throw new IllegalStateException("Fora do periodo de cancelamento");
        }
        if (m == null || !this.matriculas.contains(m)) {
            throw new IllegalArgumentException("Matricula invalida");
        }
        m.cancelar();
    }

    public List<Matricula> obterMatriculas(Curriculo c) {
        List<Matricula> res = new ArrayList<>();
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA && c.equals(m.getCurriculo())) {
                res.add(m);
            }
        }
        return res;
    }

    // Getters / Setters
    public String getMatriculaAluno() { return matriculaAluno; }
    public void setMatriculaAluno(String matriculaAluno) { this.matriculaAluno = matriculaAluno; }
    public List<Matricula> getMatriculas() { return matriculas; }
    public void setMatriculas(List<Matricula> matriculas) { this.matriculas = matriculas; }
}
