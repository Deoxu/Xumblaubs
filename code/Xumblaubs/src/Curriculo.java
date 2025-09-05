import java.util.List;
import java.util.ArrayList;

public class Curriculo {
    private Curso curso; // NOVO: currículo pertence a um curso
    private int ano;
    private int semestre;
    private List<Disciplina> disciplinas;

    // construtor com curso
    public Curriculo(Curso curso, int ano, int semestre) {
        this.curso = curso;
        this.ano = ano;
        this.semestre = semestre;
        this.disciplinas = new ArrayList<>();
    }

    // construtor sem curso (compatibilidade)
    public Curriculo(int ano, int semestre) {
        this(null, ano, semestre);
    }

    public void adicionarDisciplina(Disciplina d) {
        // validação: só aceita disciplinas do mesmo curso (se definido)
        if (this.curso != null && d.getCurso() != null && d.getCurso() != this.curso) {
            throw new IllegalArgumentException(
                "Disciplina " + d.getCodigo() + " não pertence ao curso " + this.curso.getNome()
            );
        }
        if (!this.disciplinas.contains(d)) {
            this.disciplinas.add(d);
        }
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
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    public List<Disciplina> getDisciplinas() { return disciplinas; }
    public void setDisciplinas(List<Disciplina> disciplinas) { this.disciplinas = disciplinas; }
}
