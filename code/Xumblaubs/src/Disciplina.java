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
        for (Matricula matricula : matriculas) {
            if (matricula.getStatus() == StatusMatricula.ATIVA) {
                ativos++;
            }
        }
        return ativos;
    }
    
    public void encerrarInscricoes() {
        this.ativa = false;
        // Verificar se a disciplina deve ser cancelada por falta de alunos
        if (inscritos() < minAlunos) {
            System.out.println("Disciplina " + this.nome + " cancelada por não atingir o mínimo de " + minAlunos + " alunos");
            // Cancelar todas as matrículas ativas
            for (Matricula matricula : matriculas) {
                if (matricula.getStatus() == StatusMatricula.ATIVA) {
                    matricula.cancelar();
                }
            }
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
        
        // Verificar se o aluno já está matriculado nesta disciplina
        for (Matricula matricula : matriculas) {
            if (matricula.getAluno().equals(a) && matricula.getStatus() == StatusMatricula.ATIVA) {
                throw new IllegalStateException("Aluno já está matriculado nesta disciplina");
            }
        }
        
        // Nota: O curriculo será definido pelo Aluno ao chamar matricular()
        Matricula novaMatricula = new Matricula(a, this, null, t);
        this.matriculas.add(novaMatricula);
        return novaMatricula;
    }
    
    // Getters e Setters
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public int getCreditos() {
        return creditos;
    }
    
    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }
    
    public int getMinAlunos() {
        return minAlunos;
    }
    
    public void setMinAlunos(int minAlunos) {
        this.minAlunos = minAlunos;
    }
    
    public int getMaxAlunos() {
        return maxAlunos;
    }
    
    public void setMaxAlunos(int maxAlunos) {
        this.maxAlunos = maxAlunos;
    }
    
    public List<Matricula> getMatriculas() {
        return matriculas;
    }
    
    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }
    
    public Curso getCurso() {
        return curso;
    }
    
    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}