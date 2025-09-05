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
        // 1) Verificar se a disciplina faz parte do currículo informado
        if (!c.getDisciplinas().contains(d)) {
            throw new IllegalStateException("Disciplina não ofertada neste currículo/semestre");
        }

        // 2) Verificar se está no período de matrícula
        if (!secretaria.podeMatricular()) {
            throw new IllegalStateException("Fora do período de matrícula");
        }

        // 3) Verificar limites por tipo
        int obrigatoriasAtivas = contarMatriculasAtivas(TipoDisciplina.OBRIGATORIA, c);
        int optativasAtivas = contarMatriculasAtivas(TipoDisciplina.OPTATIVA, c);

        if (t == TipoDisciplina.OBRIGATORIA && obrigatoriasAtivas >= 4) {
            throw new IllegalStateException("Limite de 4 disciplinas obrigatórias atingido");
        }
        if (t == TipoDisciplina.OPTATIVA && optativasAtivas >= 2) {
            throw new IllegalStateException("Limite de 2 disciplinas optativas atingido");
        }

        // 4) Inscrever na disciplina (duplicidade e vagas são checadas na própria disciplina)
        Matricula matricula = d.inscrever(this, t);
        matricula.setCurriculo(c);
        this.matriculas.add(matricula);

        // (IMPORTANTE) Removido: notificação de cobrança por matrícula (agora é consolidada por semestre)
        // matricula.notificarCobranca();

        return matricula;
    }

    private int contarMatriculasAtivas(TipoDisciplina tipo, Curriculo c) {
        int count = 0;
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA
                && m.getTipo() == tipo
                && c.equals(m.getCurriculo())) {
                count++;
            }
        }
        return count;
    }

    public void cancelar(Matricula m, Secretaria secretaria) {
        if (!secretaria.podeCancelar()) {
            throw new IllegalStateException("Fora do período de cancelamento");
        }
        m.cancelar();
    }

    // 2) Agora filtra por currículo informado
    public List<Matricula> obterMatriculas(Curriculo c) {
        List<Matricula> matriculasCurriculo = new ArrayList<>();
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA && c.equals(m.getCurriculo())) {
                matriculasCurriculo.add(m);
            }
        }
        return matriculasCurriculo;
    }

    // Getters e Setters
    public String getMatriculaAluno() {
        return matriculaAluno;
    }

    public void setMatriculaAluno(String matriculaAluno) {
        this.matriculaAluno = matriculaAluno;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }
}
