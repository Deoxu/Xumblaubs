import java.util.List;
import java.util.ArrayList;

public class Curriculo {
    private int ano;
    private int semestre;
    private List<Disciplina> disciplinas;
    
    public Curriculo(int ano, int semestre) {
        this.ano = ano;
        this.semestre = semestre;
        this.disciplinas = new ArrayList<>();
    }
    
    public void adicionarDisciplina(Disciplina d) {
        this.disciplinas.add(d);
    }
    
    public List<Disciplina> listarDisciplinas() {
        return new ArrayList<>(disciplinas);
    }
    
    public List<Disciplina> listarDisciplinas(Professor p) {
        List<Disciplina> disciplinasProfessor = new ArrayList<>();
        for (Disciplina disciplina : disciplinas) {
            if (p.getDisciplinasMinistradas().contains(disciplina)) {
                disciplinasProfessor.add(disciplina);
            }
        }
        return disciplinasProfessor;
    }
    
    // Getters e Setters
    public int getAno() {
        return ano;
    }
    
    public void setAno(int ano) {
        this.ano = ano;
    }
    
    public int getSemestre() {
        return semestre;
    }
    
    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
    
    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
    
    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
}

