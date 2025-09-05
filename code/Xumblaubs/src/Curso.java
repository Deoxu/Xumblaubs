import java.util.List;
import java.util.ArrayList;

public class Curso {
    private String codigo;
    private String nome;
    private int creditosTotais;
    private List<Disciplina> disciplinas;

    public Curso(String codigo, String nome, int creditosTotais) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditosTotais = creditosTotais;
        this.disciplinas = new ArrayList<>();
    }

    public void adicionarDisciplina(Disciplina d) {
        if (!disciplinas.contains(d)) {
            disciplinas.add(d);
            System.out.println("Disciplina " + d.getNome() + " adicionada ao curso " + this.nome);
        }
    }

    public void removerDisciplina(Disciplina d) {
        if (disciplinas.contains(d)) {
            disciplinas.remove(d);
            System.out.println("Disciplina " + d.getNome() + " removida do curso " + this.nome);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Curso))
            return false;
        Curso other = (Curso) o;
        return this.getCodigo() != null && this.getCodigo().equals(other.getCodigo());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getCodigo());
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

    public int getCreditosTotais() {
        return creditosTotais;
    }

    public void setCreditosTotais(int creditosTotais) {
        this.creditosTotais = creditosTotais;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
}
