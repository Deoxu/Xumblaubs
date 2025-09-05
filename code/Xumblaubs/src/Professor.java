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
        if (d == null || c == null) return alunosDisciplina;

        for (Matricula m : d.getMatriculas()) {
            if (m.getStatus() == StatusMatricula.ATIVA && c.equals(m.getCurriculo())) {
                alunosDisciplina.add(m.getAluno());
            }
        }
        System.out.println("Listando " + alunosDisciplina.size() + " aluno(s) da disciplina "
                + d.getNome() + " no curriculo " + c.getAno() + "/" + c.getSemestre());
        return alunosDisciplina;
    }

    public List<Disciplina> listarDisciplinas(Curriculo c) {
        List<Disciplina> res = new ArrayList<>();
        for (Disciplina d : disciplinasMinistradas) {
            if (c.getDisciplinas().contains(d)) {
                res.add(d);
            }
        }
        return res;
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (!disciplinasMinistradas.contains(disciplina)) {
            disciplinasMinistradas.add(disciplina);
            System.out.println("Professor " + this.getNome() + " agora ministra " + disciplina.getNome());
        }
    }

    // Getters / Setters
    public String getIdProfessor() { return idProfessor; }
    public void setIdProfessor(String idProfessor) { this.idProfessor = idProfessor; }
    public List<Disciplina> getDisciplinasMinistradas() { return disciplinasMinistradas; }
    public void setDisciplinasMinistradas(List<Disciplina> disciplinasMinistradas) { this.disciplinasMinistradas = disciplinasMinistradas; }
}
