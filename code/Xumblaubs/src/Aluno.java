import java.util.List;
import java.util.ArrayList;

public class Aluno extends Usuario implements IMatricula {
    private String matriculaAluno;
    private List<Matricula> matriculas;
    
    public Aluno(String id, String nome, String email, String senha, String matriculaAluno) {
        super(id, nome, email, senha);
        this.matriculaAluno = matriculaAluno;
        this.matriculas = new ArrayList<>();
    }
    
    public Matricula matricular(Disciplina d, Curriculo c, TipoDisciplina t) {
        Matricula matricula = d.inscrever(this, t);
        this.matriculas.add(matricula);
        return matricula;
    }
    
    public void cancelar(Matricula m) {
        m.cancelar();
    }
    
    public List<Matricula> obterMatriculas(Curriculo c) {
        List<Matricula> matriculasCurriculo = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            if (matricula.getStatus() == StatusMatricula.ATIVA) {
                matriculasCurriculo.add(matricula);
            }
        }
        return matriculasCurriculo;
    }
    
    public boolean estaMatriculado(Disciplina disciplina, Curriculo curriculo) {
        for (Matricula matricula : matriculas) {
            if (matricula.getDisciplina().equals(disciplina) && 
                matricula.getStatus() == StatusMatricula.ATIVA) {
                return true;
            }
        }
        return false;
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
