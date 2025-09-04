public class Disciplina {
    // Atributos privados
    private int minAlunos = 3;
    private int maxAlunos = 60;
    private String codigo;
    private String nome;
    private int creditos;
    private int qtdAlunos;
    private boolean ativa;

    // Construtor padrão
    public Disciplina() {
        this.ativa = true;
        this.qtdAlunos = 0;
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
        return maxAlunos - qtdAlunos;
    }

    public int inscritos() {
        return qtdAlunos;
    }

    public void encerrarInscricoes() {
        this.ativa = false;
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

        if (qtdAlunos < minAlunos) {
            throw new IllegalStateException("Número mínimo de alunos não atingido");
        }

        qtdAlunos++;
        return new Matricula(a, this, t);
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
}
