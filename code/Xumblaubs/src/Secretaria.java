import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Secretaria extends Usuario {
    private List<Aluno> alunos;
    private List<Professor> professores;
    private List<Disciplina> disciplinas;
    private List<Curriculo> curriculos;
    private List<Curso> cursos; // NOVO
    private List<PeriodoMatricula> periodosMatricula;
    private final java.util.Set<String> cobrancaEmitida = new java.util.HashSet<>();

    public Secretaria(String id, String nome, String email, String senha) {
        super(id, nome, email, senha);
        this.alunos = new ArrayList<>();
        this.professores = new ArrayList<>();
        this.disciplinas = new ArrayList<>();
        this.curriculos = new ArrayList<>();
        this.cursos = new ArrayList<>();
        this.periodosMatricula = new ArrayList<>();
    }

    /* ====================== CURSOS ====================== */

    public Curso cadastrarCurso(Curso c) {
        if (!cursos.contains(c)) {
            cursos.add(c);
            System.out.println("Curso " + c.getNome() + " cadastrado.");
        } else {
            System.out.println("Curso " + c.getNome() + " atualizado.");
        }
        return c;
    }

    public List<Curso> listarCursos() {
        return new ArrayList<>(cursos);
    }

    public List<Curso> getCursos() { // getter usado pelo FileStore
        return cursos;
    }

    /* ====================== CURRÍCULOS ====================== */

    public Curriculo gerarCurriculo(Curso curso, int ano, int semestre) {
        Curriculo novo = new Curriculo(curso, ano, semestre);
        this.curriculos.add(novo);
        System.out.println("Currículo gerado para o curso " + curso.getNome() + " - " + ano + "/" + semestre);
        return novo;
    }

    // compatibilidade (sem curso)
    public Curriculo gerarCurriculo(int ano, int semestre) {
        Curriculo novo = new Curriculo(null, ano, semestre);
        this.curriculos.add(novo);
        System.out.println("Currículo gerado (sem curso atrelado) para " + ano + "/" + semestre);
        return novo;
    }

    /* ====================== ALUNOS & PROFESSORES ====================== */

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

    /* ====================== DISCIPLINAS ====================== */

    public Disciplina cadastrarDisciplina(Disciplina d, Curso c) {
        if (!disciplinas.contains(d)) {
            disciplinas.add(d);
            d.setCurso(c);
            c.adicionarDisciplina(d);
            System.out.println("Disciplina " + d.getNome() + " cadastrada no curso " + c.getNome());
        } else {
            if (d.getCurso() == null)
                d.setCurso(c);
            if (!c.getDisciplinas().contains(d))
                c.adicionarDisciplina(d);
            System.out.println("Disciplina " + d.getNome() + " atualizada no curso " + c.getNome());
        }
        return d;
    }

    /* ====================== CONSULTAS ====================== */

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

    // ==== GETTERS usados pelo FileStore (faltavam) ====
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

    /* ====================== PERÍODOS DE MATRÍCULA ====================== */

    public void definirPeriodoMatricula(Date inicio, Date fim, TipoPeriodo tipo) {
        PeriodoMatricula periodo = new PeriodoMatricula(inicio, fim, tipo);
        this.periodosMatricula.add(periodo);
        System.out.println("Periodo de " + tipo + " definido de " + inicio + " ate " + fim);
    }

    public boolean podeMatricular() {
        for (PeriodoMatricula periodo : periodosMatricula) {
            if (periodo.podeMatricular())
                return true;
        }
        return false;
    }

    public boolean podeCancelar() {
        for (PeriodoMatricula periodo : periodosMatricula) {
            if (periodo.podeCancelar())
                return true;
        }
        return false;
    }

    public void encerrarPeriodoMatricula(Curriculo c) {
        for (Disciplina d : c.getDisciplinas()) {
            d.encerrarInscricoes();
        }
        System.out.println("Período encerrado para o currículo " + c.getAno() + "/" + c.getSemestre());
    }

    /* ====================== COBRANÇAS ====================== */

    public void notificarCobrancaDoSemestre(Aluno a, Curriculo c, ISistemaCobrancas cobrancas) {
        String chave = a.getMatriculaAluno() + ":" + c.getAno() + "/" + c.getSemestre();
        if (cobrancaEmitida.contains(chave))
            return;

        List<Disciplina> discs = new ArrayList<>();
        for (Matricula m : a.getMatriculas()) {
            if (m.getStatus() == StatusMatricula.ATIVA && c.equals(m.getCurriculo())) {
                discs.add(m.getDisciplina());
            }
        }
        if (!discs.isEmpty()) {
            cobrancas.notificarInscricao(a, c, discs);
            cobrancaEmitida.add(chave);
        }
    }
}
