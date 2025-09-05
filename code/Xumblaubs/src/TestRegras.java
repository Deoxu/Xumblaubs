import java.util.*;

/**
 * TestRegras: testa as regras principais do sistema SEM JUnit.
 * Compile e rode no mesmo lugar das suas classes:
 *
 * javac TestRegras.java
 * java TestRegras
 */
public class TestRegras {

    /* ======= Util de cobranca para checar idempotencia ======= */
    static class CobrancasMock implements ISistemaCobrancas {
        int chamadas = 0;

        @Override
        public void notificarInscricao(Aluno a, Curriculo c, List<Disciplina> disciplinas) {
            chamadas++;
            System.out.println("[COBRANCA] aluno=" + a.getNome() +
                    " curriculo=" + c.getAno() + "/" + c.getSemestre() +
                    " disciplinas=" + disciplinas.size());
        }
    }

    /* ======= Contadores e helpers ======= */
    static int OK = 0, ERRO = 0;

    static void ok(String msg) {
        System.out.println("[OK] " + msg);
        OK++;
    }

    static void err(String msg) {
        System.out.println("[ERRO] " + msg);
        ERRO++;
    }

    public static void main(String[] args) {
        System.out.println("== Iniciando TestRegras ==");

        /* ---------- Base do cenário ---------- */
        Secretaria sec = new Secretaria("SEC001", "Secretaria", "sec@u.edu", "123");

        Curso eng = new Curso("ENG001", "Engenharia de Software", 240);
        sec.cadastrarCurso(eng);

        Curriculo cur = sec.gerarCurriculo(2025, 1);
        try {
            cur.setCurso(eng);
        } catch (Throwable ignore) {
        }

        // Cria um período de MATRICULA ativo por 60s
        Date ini = new Date();
        Date fim = new Date(ini.getTime() + 60_000);
        sec.definirPeriodoMatricula(ini, fim, TipoPeriodo.MATRICULA);

        // Disciplinas base
        Disciplina d1 = disc(sec, cur, eng, "D1", "Obr1", 4);
        Disciplina d2 = disc(sec, cur, eng, "D2", "Obr2", 4);
        Disciplina d3 = disc(sec, cur, eng, "D3", "Obr3", 4);
        Disciplina d4 = disc(sec, cur, eng, "D4", "Obr4", 4);
        Disciplina d5 = disc(sec, cur, eng, "D5", "Obr5", 4);
        Disciplina o1 = disc(sec, cur, eng, "O1", "Opt1", 2);
        Disciplina o2 = disc(sec, cur, eng, "O2", "Opt2", 2);
        Disciplina o3 = disc(sec, cur, eng, "O3", "Opt3", 2);

        Aluno a = new Aluno("A1", "Ana", "ana@u", "s", "RA1");

        /* ---------- 1) 5ª obrigatória bloqueia ---------- */
        try {
            matricularOk(a, d1, cur, TipoDisciplina.OBRIGATORIA, sec);
            matricularOk(a, d2, cur, TipoDisciplina.OBRIGATORIA, sec);
            matricularOk(a, d3, cur, TipoDisciplina.OBRIGATORIA, sec);
            matricularOk(a, d4, cur, TipoDisciplina.OBRIGATORIA, sec);
            a.matricular(d5, cur, TipoDisciplina.OBRIGATORIA, sec);
            err("5a obrigatoria NAO bloqueou");
        } catch (Exception e) {
            ok("5a obrigatoria bloqueada: " + e.getMessage());
        }

        /* ---------- 2) 3ª optativa bloqueia ---------- */
        try {
            matricularOk(a, o1, cur, TipoDisciplina.OPTATIVA, sec);
            matricularOk(a, o2, cur, TipoDisciplina.OPTATIVA, sec);
            a.matricular(o3, cur, TipoDisciplina.OPTATIVA, sec);
            err("3a optativa NAO bloqueou");
        } catch (Exception e) {
            ok("3a optativa bloqueada: " + e.getMessage());
        }

        /* ---------- 3) Duplicidade mesma disciplina ---------- */
        try {
            a.matricular(d1, cur, TipoDisciplina.OBRIGATORIA, sec);
            err("Duplicidade NAO bloqueou");
        } catch (Exception e) {
            ok("Duplicidade bloqueada: " + e.getMessage());
        }

        /* ---------- 4) Capacidade 60 alunos ---------- */
        Disciplina cap = disc(sec, cur, eng, "CAP", "Capacidade", 2);
        boolean quebrouAntes = false;
        for (int i = 0; i < 60; i++) {
            try {
                Aluno ax = new Aluno("AX" + i, "Aluno" + i, "a" + i + "@u", "s", "R" + i);
                ax.matricular(cap, cur, TipoDisciplina.OBRIGATORIA, sec);
            } catch (Exception e) {
                quebrouAntes = true;
                err("Falhou antes do 60o: " + e.getMessage());
                break;
            }
        }
        if (!quebrouAntes)
            ok("Primeiros 60 matriculados em CAP");
        try {
            Aluno ax61 = new Aluno("AX61", "Aluno61", "a61@u", "s", "R61");
            ax61.matricular(cap, cur, TipoDisciplina.OBRIGATORIA, sec);
            err("61o NAO bloqueou");
        } catch (Exception e) {
            ok("61o bloqueado: " + e.getMessage());
        }

        /* ---------- 5) Cancelar fora do período bloqueia ---------- */
        try {
            // pega uma matricula ativa da Ana
            Matricula m = a.getMatriculas().stream()
                    .filter(mx -> mx.getStatus() == StatusMatricula.ATIVA)
                    .findFirst().orElseThrow();
            a.cancelar(m, sec); // ainda não há período CANCELAMENTO
            err("Cancelamento fora do periodo NAO bloqueou");
        } catch (Exception e) {
            ok("Cancelamento fora do periodo bloqueado: " + e.getMessage());
        }

        /* ---------- 6) Cancelar dentro do período funciona ---------- */
        Date cini = new Date();
        Date cfim = new Date(cini.getTime() + 60_000);
        sec.definirPeriodoMatricula(cini, cfim, TipoPeriodo.CANCELAMENTO);

        Matricula m2 = a.getMatriculas().stream()
                .filter(mx -> mx.getStatus() == StatusMatricula.ATIVA)
                .findFirst().orElse(null);
        if (m2 != null) {
            try {
                a.cancelar(m2, sec);
                if (m2.getStatus() == StatusMatricula.CANCELADA)
                    ok("Cancelamento no periodo ocorreu");
                else
                    err("Cancelamento no periodo NAO mudou status");
            } catch (Exception e) {
                err("Cancelamento no periodo falhou: " + e.getMessage());
            }
        } else {
            err("Sem matricula ativa para testar cancelamento no periodo");
        }

        /*
         * ---------- 7) Matricula fora do período bloqueia ----------
         * Usamos outra Secretaria apenas com periodo expirado
         */
        Secretaria secFora = new Secretaria("SECX", "SecX", "x@u", "x");
        Date eIni = new Date(System.currentTimeMillis() - 120_000);
        Date eFim = new Date(System.currentTimeMillis() - 60_000);
        secFora.definirPeriodoMatricula(eIni, eFim, TipoPeriodo.MATRICULA);

        Disciplina fora = disc(sec, cur, eng, "FORA", "ForaPeriodo", 2);
        try {
            a.matricular(fora, cur, TipoDisciplina.OPTATIVA, secFora);
            err("Matricula fora do periodo NAO bloqueou");
        } catch (Exception e) {
            ok("Matricula fora do periodo bloqueada: " + e.getMessage());
        }

        /* ---------- 8) Encerrar: disciplina com <3 cancelada ---------- */
        Disciplina min3 = disc(sec, cur, eng, "MIN3", "MinTres", 2);
        // garante matricula ativa para 2 alunos
        Aluno b = new Aluno("B1", "Bia", "bia@u", "s", "RB1");
        Aluno c = new Aluno("C1", "Caio", "caio@u", "s", "RC1");
        try {
            // abre mais um período curto se o anterior expirou
            sec.definirPeriodoMatricula(new Date(), new Date(System.currentTimeMillis() + 60_000),
                    TipoPeriodo.MATRICULA);
            b.matricular(min3, cur, TipoDisciplina.OBRIGATORIA, sec);
            c.matricular(min3, cur, TipoDisciplina.OBRIGATORIA, sec);
        } catch (Exception ex) {
            // ignora se já estava aberto
        }
        sec.encerrarPeriodoMatricula(cur);
        if (!min3.estaAtiva())
            ok("Disciplina MIN3 cancelada por <3 alunos");
        else
            err("Disciplina MIN3 NAO foi cancelada com apenas 2 alunos");

        /* ---------- 9) Professor lista alunos ---------- */
        // cria uma disciplina nova e matricula 1 aluno nela
        Disciplina lst = disc(sec, cur, eng, "LST", "Listagem", 2);
        try {
            sec.definirPeriodoMatricula(new Date(), new Date(System.currentTimeMillis() + 60_000),
                    TipoPeriodo.MATRICULA);
            a.matricular(lst, cur, TipoDisciplina.OBRIGATORIA, sec);
        } catch (Exception ignore) {
        }
        Professor prof = new Professor("P1", "Prof", "prof@u", "s", "P1");
        sec.cadastrarProfessor(prof);
        prof.adicionarDisciplina(lst);
        List<Aluno> alunos = prof.listarAlunos(lst, cur);
        if (alunos != null && !alunos.isEmpty())
            ok("Professor listou " + alunos.size() + " aluno(s)");
        else
            err("Professor NAO listou alunos");

        /* ---------- 10) Cobranca idempotente ---------- */
        CobrancasMock mock = new CobrancasMock();
        Disciplina pag = disc(sec, cur, eng, "PAG", "Pagavel", 4);
        try {
            sec.definirPeriodoMatricula(new Date(), new Date(System.currentTimeMillis() + 60_000),
                    TipoPeriodo.MATRICULA);
            a.matricular(pag, cur, TipoDisciplina.OBRIGATORIA, sec);
        } catch (Exception ignore) {
        }
        sec.notificarCobrancaDoSemestre(a, cur, mock);
        sec.notificarCobrancaDoSemestre(a, cur, mock);
        if (mock.chamadas == 1)
            ok("Cobranca idempotente OK");
        else
            err("Cobranca NAO idempotente: chamadas=" + mock.chamadas);

        /* ---------- Resultado ---------- */
        System.out.println("== Fim TestRegras ==  OK=" + OK + "  ERRO=" + ERRO);
        if (ERRO > 0)
            System.exit(1);
    }

    /* ======= Helpers específicos ======= */

    private static Disciplina disc(Secretaria sec, Curriculo cur, Curso curso,
            String cod, String nome, int creditos) {
        Disciplina d = new Disciplina(cod, nome, creditos);
        sec.cadastrarDisciplina(d, curso);
        try {
            cur.adicionarDisciplina(d);
        } catch (Exception e) {
            // se a regra de curso bloquear por curso diferente, apenas loga
            System.out.println("Aviso add disciplina ao curriculo: " + e.getMessage());
        }
        return d;
    }

    private static void matricularOk(Aluno a, Disciplina d, Curriculo c,
            TipoDisciplina t, Secretaria sec) {
        try {
            a.matricular(d, c, t, sec);
        } catch (Exception e) {
            throw new RuntimeException("Falhou matricular " + d.getCodigo() + ": " + e.getMessage());
        }
    }
}
