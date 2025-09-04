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
        // Verificar se está no período de matrícula
        if (!secretaria.podeMatricular()) {
            throw new IllegalStateException("Fora do período de matrícula");
        }
        
        // Verificar limites de matrícula
        int obrigatoriasAtivas = contarMatriculasAtivas(TipoDisciplina.OBRIGATORIA);
        int optativasAtivas = contarMatriculasAtivas(TipoDisciplina.OPTATIVA);
        
        if (t == TipoDisciplina.OBRIGATORIA && obrigatoriasAtivas >= 4) {
            throw new IllegalStateException("Limite de 4 disciplinas obrigatórias atingido");
        }
        
        if (t == TipoDisciplina.OPTATIVA && optativasAtivas >= 2) {
            throw new IllegalStateException("Limite de 2 disciplinas optativas atingido");
        }
        
        Matricula matricula = d.inscrever(this, t);
        matricula.setCurriculo(c);
        this.matriculas.add(matricula);
        
        // Notificar sistema de cobrança
        matricula.notificarCobranca();
        
        return matricula;
    }
    
    private int contarMatriculasAtivas(TipoDisciplina tipo) {
        int count = 0;
        for (Matricula matricula : matriculas) {
            if (matricula.getStatus() == StatusMatricula.ATIVA && matricula.getTipo() == tipo) {
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
    
    public List<Matricula> obterMatriculas(Curriculo c) {
        List<Matricula> matriculasCurriculo = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            if (matricula.getStatus() == StatusMatricula.ATIVA) {
                matriculasCurriculo.add(matricula);
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
