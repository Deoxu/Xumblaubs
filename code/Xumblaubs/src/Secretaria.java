import java.util.List;
import java.util.ArrayList;

public class Secretaria extends Usuario implements IGestaoAcademica {
    private List<Aluno> alunos;
    private List<Professor> professores;
    private List<Disciplina> disciplinas;
    private List<Curriculo> curriculos;
    
    public Secretaria(String id, String nome, String email, String senha) {
        super(id, nome, email, senha);
        this.alunos = new ArrayList<>();
        this.professores = new ArrayList<>();
        this.disciplinas = new ArrayList<>();
        this.curriculos = new ArrayList<>();
    }
    
    public Curriculo gerarCurriculo(int ano, int semestre) {
        Curriculo novoCurriculo = new Curriculo(ano, semestre);
        this.curriculos.add(novoCurriculo);
        System.out.println("Currículo gerado para " + ano + "/" + semestre);
        return novoCurriculo;
    }
    
    public Aluno cadastrarAluno(Aluno a) {
        if (alunos.contains(a)) {
            System.out.println("Aluno " + a.getNome() + " atualizado no sistema");
        } else {
            alunos.add(a);
            System.out.println("Aluno " + a.getNome() + " cadastrado no sistema");
        }
        return a;
    }
    
    public Professor cadastrarProfessor(Professor p) {
        if (professores.contains(p)) {
            System.out.println("Professor " + p.getNome() + " atualizado no sistema");
        } else {
            professores.add(p);
            System.out.println("Professor " + p.getNome() + " cadastrado no sistema");
        }
        return p;
    }
    
    public Disciplina cadastrarDisciplina(Disciplina d, Curso c) {
        if (disciplinas.contains(d)) {
            System.out.println("Disciplina " + d.getNome() + " atualizada no sistema");
        } else {
            disciplinas.add(d);
            d.setCurso(c);
            c.adicionarDisciplina(d);
            System.out.println("Disciplina " + d.getNome() + " cadastrada no sistema e associada ao curso " + c.getNome());
        }
        return d;
    }
    
    // Métodos auxiliares para consulta
    public List<Aluno> listarAlunos() {
        return new ArrayList<>(alunos);
    }
    
    public List<Professor> listarProfessores() {
        return new ArrayList<>(professores);
    }
    
    public List<Disciplina> listarDisciplinas() {
        return new ArrayList<>(disciplinas);
    }
    
    public List<Curriculo> listarCurriculos() {
        return new ArrayList<>(curriculos);
    }
    
    // Getters
    public List<Aluno> getAlunos() {
        return alunos;
    }
    
    public List<Professor> getProfessores() {
        return professores;
    }
    
    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
    
    public List<Curriculo> getCurriculos() {
        return curriculos;
    }
}
