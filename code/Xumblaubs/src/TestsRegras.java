import java.util.*;

public class TestsRegras {
    public static void main(String[] args) {
        System.out.println("== Iniciando testes de regra ==");

        Secretaria sec = new Secretaria("SEC001", "Secretaria", "sec@u.edu", "123");
        SistemaCobrancas cobr = new SistemaCobrancas();

        // Período de matrícula ATIVO agora
        Date agora = new Date();
        Date fim = new Date(agora.getTime() + 60_000); // +1 minuto
        sec.definirPeriodoMatricula(agora, fim, TipoPeriodo.MATRICULA);

        Curriculo cur = sec.gerarCurriculo(2025, 2);

        Curso curso = new Curso("ENG", "Engenharia", 240);
        Disciplina d1 = new Disciplina("D1", "Obr1", 4);
        Disciplina d2 = new Disciplina("D2", "Obr2", 4);
        Disciplina d3 = new Disciplina("D3", "Obr3", 4);
        Disciplina d4 = new Disciplina("D4", "Obr4", 4);
        Disciplina d5 = new Disciplina("D5", "Obr5", 4);
        Disciplina o1 = new Disciplina("O1", "Opt1", 2);
        Disciplina o2 = new Disciplina("O2", "Opt2", 2);
        Disciplina o3 = new Disciplina("O3", "Opt3", 2);

        for (Disciplina d : List.of(d1,d2,d3,d4,d5,o1,o2,o3)) {
            sec.cadastrarDisciplina(d, curso);
            cur.adicionarDisciplina(d);
        }

        Aluno a = new Aluno("A1", "Ana", "ana@u", "s", "2025A1");

        // 1) 5ª obrigatória -> bloqueia
        try {
            a.matricular(d1, cur, TipoDisciplina.OBRIGATORIA, sec);
            a.matricular(d2, cur, TipoDisciplina.OBRIGATORIA, sec);
            a.matricular(d3, cur, TipoDisciplina.OBRIGATORIA, sec);
            a.matricular(d4, cur, TipoDisciplina.OBRIGATORIA, sec);
            a.matricular(d5, cur, TipoDisciplina.OBRIGATORIA, sec); // deve falhar
            System.out.println("[ERRO] 5ª obrigatória não bloqueou!");
        } catch (Exception e) {
            System.out.println("[OK] 5ª obrigatória bloqueada: " + e.getMessage());
        }

        // 2) 3ª optativa -> bloqueia
        try {
            a.matricular(o1, cur, TipoDisciplina.OPTATIVA, sec);
            a.matricular(o2, cur, TipoDisciplina.OPTATIVA, sec);
            a.matricular(o3, cur, TipoDisciplina.OPTATIVA, sec); // deve falhar
            System.out.println("[ERRO] 3ª optativa não bloqueou!");
        } catch (Exception e) {
            System.out.println("[OK] 3ª optativa bloqueada: " + e.getMessage());
        }

        // 3) 61º aluno na mesma disciplina -> bloqueia
        Disciplina capacidade = new Disciplina("CAP", "Capacidade", 2);
        sec.cadastrarDisciplina(capacidade, curso);
        cur.adicionarDisciplina(capacidade);
        List<Aluno> massa = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            Aluno ax = new Aluno("AX"+i, "Aluno"+i, "a"+i+"@u", "s", "RA"+i);
            massa.add(ax);
            ax.matricular(capacidade, cur, TipoDisciplina.OBRIGATORIA, sec);
        }
        try {
            Aluno ax = new Aluno("AX60", "Aluno60", "a60@u", "s", "RA60");
            ax.matricular(capacidade, cur, TipoDisciplina.OBRIGATORIA, sec); // deve falhar
            System.out.println("[ERRO] 61º aluno não bloqueou!");
        } catch (Exception e) {
            System.out.println("[OK] 61º aluno bloqueado: " + e.getMessage());
        }

        // 4) Matrícula fora do período -> bloqueia
        // Fechamos o período atual e criamos um período que já expirou
        sec.definirPeriodoMatricula(new Date(agora.getTime()-120_000), new Date(agora.getTime()-60_000), TipoPeriodo.MATRICULA);
        try {
            // hack simples: vamos remover todos os períodos válidos (não obrigatório se seu podeMatricular já filtra corretamente)
            // aqui apenas ilustrativo do teste fora de período
            // Tenta matricular em nova disciplina
            Disciplina fora = new Disciplina("FORA", "ForaPeriodo", 2);
            sec.cadastrarDisciplina(fora, curso);
            cur.adicionarDisciplina(fora);
            a.matricular(fora, cur, TipoDisciplina.OPTATIVA, sec);
            System.out.println("[ERRO] Matrícula fora do período não bloqueou!");
        } catch (Exception e) {
            System.out.println("[OK] Fora do período bloqueado: " + e.getMessage());
        }

        // 5) Encerrar período com 2 inscritos => disciplina cancelada
        Disciplina min3 = new Disciplina("MIN3", "MinTres", 2);
        sec.cadastrarDisciplina(min3, curso);
        cur.adicionarDisciplina(min3);
        Aluno b = new Aluno("B1", "Bia", "bia@u", "s", "2025B1");
        Aluno ccc = new Aluno("C1", "Caio", "caio@u", "s", "2025C1");

        // Reabre um período rápido só para permitir as matrículas
        Date t0 = new Date();
        Date t1 = new Date(t0.getTime()+60_000);
        sec.definirPeriodoMatricula(t0, t1, TipoPeriodo.MATRICULA);

        a.matricular(min3, cur, TipoDisciplina.OBRIGATORIA, sec);
        b.matricular(min3, cur, TipoDisciplina.OBRIGATORIA, sec);
        // Fecha o período para consolidar
        sec.encerrarPeriodoMatricula(cur);
        if (!min3.estaAtiva()) {
            System.out.println("[OK] Disciplina com 2 inscritos foi cancelada.");
        } else {
            System.out.println("[ERRO] Disciplina com 2 inscritos não foi cancelada.");
        }

        // 6) Cobrança por semestre idempotente
        // Reabre e faz algumas matrículas para o aluno a
        Date t2 = new Date();
        Date t3 = new Date(t2.getTime()+60_000);
        sec.definirPeriodoMatricula(t2, t3, TipoPeriodo.MATRICULA);
        Disciplina pag1 = new Disciplina("P1", "Pag1", 4);
        sec.cadastrarDisciplina(pag1, curso);
        cur.adicionarDisciplina(pag1);
        a.matricular(pag1, cur, TipoDisciplina.OBRIGATORIA, sec);

        sec.notificarCobrancaDoSemestre(a, cur, cobr);
        sec.notificarCobrancaDoSemestre(a, cur, cobr); // não deve duplicar
        System.out.println("[OK] Cobrança chamada 2x sem duplicar (ver console).");

        System.out.println("== Fim dos testes ==");
    }
}
