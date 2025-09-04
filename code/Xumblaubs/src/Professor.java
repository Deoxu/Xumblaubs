import java.util.List;
import java.util.ArrayList;

public class Professor extends Usuario {
    private String idProfessor;
    private List<Disciplina> disciplinasMinistradas;
    
    public Professor(String id, String nome, String email, String senha, String idProfessor) {
        super(id, nome, email, senha);
        this.idProfessor = idProfessor;
        this.disciplinasMinistradas = new ArrayList<>();
    }
    
    public List<Aluno> listarAlunos(Disciplina d, Curriculo c) {
        List<Aluno> alunosDisciplina = new ArrayList<>();
        
        // Buscar alunos matriculados na disciplina
        for (Matricula matricula : d.getMatriculas()) {
            if (matricula.getStatus() == StatusMatricula.ATIVA) {
                alunosDisciplina.add(matricula.getAluno());
            }
        }
        
        System.out.println("Listando " + alunosDisciplina.size() + " alunos da disciplina " + d.getNome() + " no currículo " + c.getAno() + "/" + c.getSemestre());
        return alunosDisciplina;
    }
    
    public List<Disciplina> listarDisciplinas(Curriculo c) {
        List<Disciplina> disciplinasCurriculo = new ArrayList<>();
        for (Disciplina disciplina : disciplinasMinistradas) {
            if (c.getDisciplinas().contains(disciplina)) {
                disciplinasCurriculo.add(disciplina);
            }
        }
        return disciplinasCurriculo;
    }
    
    public void adicionarDisciplina(Disciplina disciplina) {
        if (!disciplinasMinistradas.contains(disciplina)) {
            disciplinasMinistradas.add(disciplina);
            System.out.println("Professor " + this.getNome() + " agora ministra " + disciplina.getNome());
        }
    }
    
    // Getters e Setters
    public String getIdProfessor() {
        return idProfessor;
    }
    
    public void setIdProfessor(String idProfessor) {
        this.idProfessor = idProfessor;
    }
    
    public List<Disciplina> getDisciplinasMinistradas() {
        return disciplinasMinistradas;
    }
    
    public void setDisciplinasMinistradas(List<Disciplina> disciplinasMinistradas) {
        this.disciplinasMinistradas = disciplinasMinistradas;
    }
}
