import java.util.List;
import java.util.ArrayList;

public class Disciplina {
    private int minAlunos = 3;
    private int maxAlunos = 60;
    private String codigo;
    private String nome;
    private int creditos;
    private boolean ativa;
    private List<Matricula> matriculas;
    private Curso curso;

    public Disciplina() {
        this.ativa = true;
        this.matriculas = new ArrayList<>();
    }

    public Disciplina(String codigo, String nome, int creditos) {
        this();
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
    }

    public int vagasDisponiveis() {
        return maxAlunos - inscritos();
    }

    public int inscritos() {
        int ativos = 0;
        for (Matricula m : matriculas) {
            if (m.getStatus() == StatusMatricula.ATIVA) ativos++;
        }
        return ativos;
    }

    public void encerrarInscricoes() {
        // Se nao atingir o minimo, a disciplina NAO vai ocorrer: desativa e cancela matrículas
        if (inscritos() < minAlunos) {
            this.ativa = false;
            System.out.println("Disciplina " + this.nome + " cancelada por nao atingir o minimo de " + minAlunos + " alunos");
            for (Matricula m : matriculas) {
                if (m.getStatus() == StatusMatricula.ATIVA) {
                    m.cancelar();
                }
            }
        } else {
            // Atingiu o minimo: confirmada para o proximo semestre
            this.ativa = true;
        }
    }

    public boolean estaAtiva() {
        return ativa;
    }

    public Matricula inscrever(Aluno a, TipoDisciplina t) {
        if (!estaAtiva()) {
            throw new IllegalStateException("Disciplina nao esta ativa para inscricoes");
        }
        if (vagasDisponiveis() <= 0) {
            throw new IllegalStateException("Nao ha vagas disponiveis");
        }
        // impedir duplicidade na mesma disciplina
        for (Matricula m : matriculas) {
            if (m.getAluno().equals(a) && m.getStatus() == StatusMatricula.ATIVA) {
                throw new IllegalStateException("Aluno ja esta matriculado nesta disciplina");
            }
        }
        Matricula nova = new Matricula(a, this, null, t); // curriculo sera setado pelo Aluno
        this.matriculas.add(nova);
        return nova;
    }

    // Getters / Setters
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
