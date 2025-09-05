import java.util.Date;

public class Matricula {
    private Aluno aluno;
    private Disciplina disciplina;
    private Curriculo curriculo;
    private Date data;
    private TipoDisciplina tipo;
    private StatusMatricula status;
    private static ISistemaCobrancas sistemaCobrancas;
    
    public Matricula(Aluno aluno, Disciplina disciplina, Curriculo curriculo, TipoDisciplina tipo) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.curriculo = curriculo;
        this.tipo = tipo;
        this.data = new Date();
        this.status = StatusMatricula.ATIVA;
    }
    
    public static void setSistemaCobrancas(ISistemaCobrancas sistema) {
        sistemaCobrancas = sistema;
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
    
    public Curriculo getCurriculo() {
        return curriculo;
    }
    
    public void setCurriculo(Curriculo curriculo) {
        this.curriculo = curriculo;
    }
    
    public void notificarCobranca() {
        if (sistemaCobrancas != null && this.status == StatusMatricula.ATIVA) {
            java.util.List<Disciplina> disciplinas = new java.util.ArrayList<>();
            disciplinas.add(this.disciplina);
            sistemaCobrancas.notificarInscricao(this.aluno, this.curriculo, disciplinas);
        }
    }
}
