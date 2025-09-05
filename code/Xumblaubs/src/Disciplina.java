import java.util.List;
import java.util.ArrayList;

public class Disciplina {
    // Atributos privados
    private int minAlunos = 3;
    private int maxAlunos = 60;
    private String codigo;
    private String nome;
    private int creditos;
    private boolean ativa;
    private List<Matricula> matriculas;
    private Curso curso;

    // Construtor padrão
    public Disciplina() {
        this.ativa = true;
        this.matriculas = new ArrayList<>();
    }

    // Construtor com parâmetros
    public Disciplina(String codigo, String nome, int creditos) {
        this();
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
    }

    // Métodos públicos
    public int vagasDisponiveis() {
        return maxAlunos - inscritos();
    }

    public int inscritos() {
        int ativos = 0;
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA) {
                ativos++;
            }
        }
        return ativos;
    }

    // 3) Ajustado: confirma ativa se >= minAlunos; cancela e encerra se < minAlunos
    public void encerrarInscricoes() {
        if (inscritos() < minAlunos) {
            this.ativa = false;
            System.out.println("Disciplina " + this.nome + " cancelada por não atingir o mínimo de " + minAlunos + " alunos");
            for (Matricula m : matriculas) {
                if (m.getStatus() == StatusMatricula.ATIVA) {
                    m.cancelar();
                }
            }
        } else {
            this.ativa = true;
            System.out.println("Disciplina " + this.nome + " confirmada para o semestre (>= " + minAlunos + " inscritos)");
        }
    }

    public boolean estaAtiva() {
        return ativa;
    }

    public Matricula inscrever(Aluno a, TipoDisciplina t) {
        if (!estaAtiva()) {
            throw new IllegalStateException("Disciplina não está ativa para inscrições");
        }
        if (vagasDisponiveis() <= 0) {
            throw new IllegalStateException("Não há vagas disponíveis");
        }
        // Prevenir duplicidade de matrícula ativa do mesmo aluno nesta disciplina
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA && m.getAluno().equals(a)) {
                throw new IllegalStateException("Aluno já está matriculado nesta disciplina");
            }
        }

        // Obs.: o Curriculo será definido pelo Aluno ao chamar matricular()
        Matricula novaMatricula = new Matricula(a, this, null, t);
        this.matriculas.add(novaMatricula);
        return novaMatricula;
    }

    // Getters e Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public int getMinAlunos() { return minAlunos; }
    public void setMinAlunos(int minAlunos) { this.minAlunos = minAlunos; }

    public int getMaxAlunos() { return maxAlunos; }
    public void setMaxAlunos(int maxAlunos) { this.maxAlunos = maxAlunos; }

    public List<Matricula> getMatriculas() { return matriculas; }
    public void setMatriculas(List<Matricula> matriculas) { this.matriculas = matriculas; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    
}
