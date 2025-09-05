import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainCLI {
    public static void main(String[] args) {
        // Secretaria "admin/admin"
        Secretaria secretaria = new Secretaria("SEC001", "admin", "admin", "admin");

        // 1) Importa CSVs existentes ANTES de qualquer cadastro
        FileStore.importarCSV(secretaria);

        // 2) Semeia dados mínimos para teste (sem duplicar se já existirem)
        Curso eng = getOrCreateCurso(secretaria, "ENG001", "Engenharia de Software", 240);
        Curso adm = getOrCreateCurso(secretaria, "ADM001", "Administracao", 200);

        // Disciplinas ENG
        Disciplina mat1  = getOrCreateDisciplina(secretaria, eng, "MAT001",  "Calculo I",            4);
        Disciplina fis1  = getOrCreateDisciplina(secretaria, eng, "FIS001",  "Fisica I",             4);
        Disciplina prog1 = getOrCreateDisciplina(secretaria, eng, "PROG001", "Programacao I",        6);

        // Disciplinas ADM
        Disciplina adm1  = getOrCreateDisciplina(secretaria, adm, "ADM101",  "Introducao a Adm",     4);
        Disciplina eco1  = getOrCreateDisciplina(secretaria, adm, "ECO101",  "Economia I",           4);

        // Curriculos (2025/1) e (2025/2) para ENG; (2025/1) para ADM
        Curriculo eng2025_1 = getOrCreateCurriculo(secretaria, eng, 2025, 1);
        Curriculo eng2025_2 = getOrCreateCurriculo(secretaria, eng, 2025, 2);
        Curriculo adm2025_1 = getOrCreateCurriculo(secretaria, adm, 2025, 1);

        // Adiciona as disciplinas aos curriculos correspondentes (só se ainda não estiverem)
        addDiscSafe(eng2025_1, mat1);
        addDiscSafe(eng2025_1, fis1);
        addDiscSafe(eng2025_1, prog1);

        addDiscSafe(adm2025_1, adm1);
        addDiscSafe(adm2025_1, eco1);

        // 3) Cria usuários padrão (se não existirem)
        getOrCreateProfessor(secretaria, "PROF001", "Prof Padrão", "prof@universidade.edu", "prof", "PROF001",
                List.of(mat1, fis1, prog1)); // ministra ENG

        getOrCreateAluno(secretaria, "ALU001", "Aluno Padrão", "aluno@estudante.edu", "aluno", "2025A001");

        // 4) Define períodos ativos de matrícula e cancelamento (agora → +7 dias)
        Date inicio = new Date();
        Date fim = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        ensurePeriodo(secretaria, inicio, fim, TipoPeriodo.MATRICULA);
        ensurePeriodo(secretaria, inicio, fim, TipoPeriodo.CANCELAMENTO);

        // 5) Dica de logins na tela
        System.out.println("\n=== DADOS INICIAIS PARA TESTE ===");
        System.out.println("Secretaria: admin / admin");
        System.out.println("Professor : prof@universidade.edu / prof");
        System.out.println("Aluno     : aluno@estudante.edu / aluno");
        System.out.println("Curriculos:");
        System.out.println(" - ENG001 2025/1  (MAT001, FIS001, PROG001)");
        System.out.println(" - ADM001 2025/1  (ADM101, ECO101)");
        System.out.println("Periodos ativos: MATRICULA e CANCELAMENTO de " + inicio + " ate " + fim);
        System.out.println("=================================\n");

        // 6) Inicia o CLI
        ISistemaCobrancas cobrancas = new SistemaCobrancas();
        CLI cli = new CLI(secretaria, cobrancas);
        cli.start();
    }

    /* =================== Helpers de inicialização =================== */

    private static Curso getOrCreateCurso(Secretaria sec, String codigo, String nome, int cred) {
        for (Curso c : sec.getCursos()) {
            if (codigo.equalsIgnoreCase(c.getCodigo())) return c;
        }
        Curso novo = new Curso(codigo, nome, cred);
        sec.cadastrarCurso(novo);
        return novo;
    }

    private static Disciplina getOrCreateDisciplina(Secretaria sec, Curso curso,
                                                    String codigo, String nome, int cred) {
        for (Disciplina d : sec.getDisciplinas()) {
            if (codigo.equalsIgnoreCase(d.getCodigo())) return d;
        }
        Disciplina nova = new Disciplina(codigo, nome, cred);
        sec.cadastrarDisciplina(nova, curso);
        return nova;
    }

    private static Curriculo getOrCreateCurriculo(Secretaria sec, Curso curso, int ano, int semestre) {
        // Procura por ano/semestre e mesmo curso
        for (Curriculo c : sec.getCurriculos()) {
            if (c.getAno() == ano && c.getSemestre() == semestre) {
                try {
                    if (c.getCurso() == null) c.setCurso(curso);
                } catch (Throwable ignore) {}
                if (c.getCurso() == null || c.getCurso().getCodigo().equalsIgnoreCase(curso.getCodigo())) {
                    return c;
                }
            }
        }
        // Cria e associa curso
        Curriculo novo = sec.gerarCurriculo(ano, semestre);
        try { novo.setCurso(curso); } catch (Throwable ignore) {}
        return novo;
    }

    private static void addDiscSafe(Curriculo c, Disciplina d) {
        try {
            if (!c.getDisciplinas().contains(d)) c.adicionarDisciplina(d);
        } catch (Exception e) {
            System.out.println("Aviso ao adicionar " + d.getCodigo() + " ao curriculo "
                    + c.getAno() + "/" + c.getSemestre() + ": " + e.getMessage());
        }
    }

    private static void getOrCreateProfessor(Secretaria sec, String id, String nome, String email,
                                             String senha, String idProfessor, List<Disciplina> ministra) {
        for (Professor p : sec.getProfessores()) {
            if (email.equalsIgnoreCase(p.getEmail())) return;
        }
        Professor novo = new Professor(id, nome, email, senha, idProfessor);
        sec.cadastrarProfessor(novo);
        if (ministra != null) {
            for (Disciplina d : ministra) {
                try { novo.adicionarDisciplina(d); } catch (Exception ignore) {}
            }
        }
    }

    private static void getOrCreateAluno(Secretaria sec, String id, String nome, String email,
                                         String senha, String matricula) {
        for (Aluno a : sec.getAlunos()) {
            if (email.equalsIgnoreCase(a.getEmail())) return;
        }
        sec.cadastrarAluno(new Aluno(id, nome, email, senha, matricula));
    }

    private static void ensurePeriodo(Secretaria sec, Date ini, Date fim, TipoPeriodo tipo) {
        // Se já existir um período ativo desse tipo, não cria outro
        boolean jaTem = false;
        if (tipo == TipoPeriodo.MATRICULA && sec.podeMatricular()) jaTem = true;
        if (tipo == TipoPeriodo.CANCELAMENTO && sec.podeCancelar()) jaTem = true;
        if (!jaTem) {
            try {
                sec.definirPeriodoMatricula(ini, fim, tipo);
            } catch (Exception e) {
                System.out.println("Falha ao definir periodo " + tipo + ": " + e.getMessage());
            }
        }
    }
}
