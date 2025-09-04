import java.util.Date;

public class Matricula {
    private Aluno aluno;
    private Disciplina disciplina;
    private Date data;
    private TipoDisciplina tipo;
    private StatusMatricula status;

    public Matricula(Aluno aluno, Disciplina disciplina, TipoDisciplina tipo) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
        this.data = new Date();
        this.status = StatusMatricula.ATIVA;
    }

    public void cancelar() {
        this.status = StatusMatricula.CANCELADA;
    }

    public boolean ehObrigatoria() {
        return this.tipo == TipoDisciplina.OBRIGATORIA;
    }

    // Getters e Setters
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public TipoDisciplina getTipo() {
        return tipo;
    }

    public void setTipo(TipoDisciplina tipo) {
        this.tipo = tipo;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }
}
