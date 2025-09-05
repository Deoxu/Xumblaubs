import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Persistência simples em CSV na pasta ./data
 * Exporta/Importa:
 *  - alunos.csv(id;nome;email;senha;matricula)
 *  - professores.csv(id;nome;email;senha;idProfessor)
 *  - cursos.csv(codigo;nome;creditosTotais)
 *  - disciplinas.csv(codigo;nome;creditos;ativa;inscritos;cursoCodigo)
 *  - curriculos.csv(cursoCodigo;ano;semestre;disciplinasCSV)
 */
public final class FileStore {
    private FileStore(){}

    private static final Path DIR = Paths.get("data");
    private static Path p(String name){ return DIR.resolve(name); }

    /* ========================= EXPORTAÇÃO ========================= */

    public static void exportarCSV(Secretaria sec) {
        try {
            if (!Files.exists(DIR)) Files.createDirectories(DIR);

            exportAlunos(sec.getAlunos());
            exportProfessores(sec.getProfessores());
            exportCursos(sec.getCursos());
            exportDisciplinas(sec.getDisciplinas());
            exportCurriculos(sec.getCurriculos());
        } catch (IOException e) {
            System.out.println("Falha ao exportar CSV: " + e.getMessage());
        }
    }

    private static void exportAlunos(List<Aluno> alunos) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(p("alunos.csv"), StandardCharsets.UTF_8)) {
            w.write("id;nome;email;senha;matricula\n");
            for (Aluno a : alunos) {
                w.write(s(a.getId()) + ";" + s(a.getNome()) + ";" + s(a.getEmail()) + ";" +
                        s(a.getSenha()) + ";" + s(a.getMatriculaAluno()) + "\n");
            }
        }
    }

    private static void exportProfessores(List<Professor> profs) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(p("professores.csv"), StandardCharsets.UTF_8)) {
            w.write("id;nome;email;senha;idProfessor\n");
            for (Professor p : profs) {
                w.write(s(p.getId()) + ";" + s(p.getNome()) + ";" + s(p.getEmail()) + ";" +
                        s(p.getSenha()) + ";" + s(p.getIdProfessor()) + "\n");
            }
        }
    }

    private static void exportCursos(List<Curso> cursos) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(p("cursos.csv"), StandardCharsets.UTF_8)) {
            w.write("codigo;nome;creditosTotais\n");
            for (Curso c : cursos) {
                w.write(s(c.getCodigo()) + ";" + s(c.getNome()) + ";" + c.getCreditosTotais() + "\n");
            }
        }
    }

    private static void exportDisciplinas(List<Disciplina> dis) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(p("disciplinas.csv"), StandardCharsets.UTF_8)) {
            w.write("codigo;nome;creditos;ativa;inscritos;cursoCodigo\n");
            for (Disciplina d : dis) {
                String cursoCod = d.getCurso() != null ? s(d.getCurso().getCodigo()) : "";
                w.write(s(d.getCodigo()) + ";" + s(d.getNome()) + ";" + d.getCreditos() + ";" +
                        d.estaAtiva() + ";" + d.inscritos() + ";" + cursoCod + "\n");
            }
        }
    }

    private static void exportCurriculos(List<Curriculo> curriculos) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(p("curriculos.csv"), StandardCharsets.UTF_8)) {
            w.write("cursoCodigo;ano;semestre;disciplinas\n");
            for (Curriculo c : curriculos) {
                String cursoCod = (c.getCurso() != null) ? s(c.getCurso().getCodigo()) : "";
                StringBuilder sb = new StringBuilder();
                for (Disciplina d : c.getDisciplinas()) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(s(d.getCodigo()));
                }
                w.write(cursoCod + ";" + c.getAno() + ";" + c.getSemestre() + ";" + sb + "\n");
            }
        }
    }

    /* ========================= IMPORTAÇÃO ========================= */

    public static void importarCSV(Secretaria sec) {
        try {
            importarAlunos(sec);
            importarProfessores(sec);
            importarCursos(sec);
            importarDisciplinas(sec);
            importarCurriculos(sec);
        } catch (IOException e) {
            System.out.println("Falha ao importar CSV: " + e.getMessage());
        }
    }

    private static void importarAlunos(Secretaria sec) throws IOException {
        Path f = p("alunos.csv");
        if (!Files.exists(f)) return;
        try (BufferedReader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                String[] t = line.split(";", -1);
                if (t.length < 5) continue;
                Aluno a = new Aluno(t[0].trim(), t[1].trim(), t[2].trim(), t[3].trim(), t[4].trim());
                sec.cadastrarAluno(a);
            }
        }
    }

    private static void importarProfessores(Secretaria sec) throws IOException {
        Path f = p("professores.csv");
        if (!Files.exists(f)) return;
        try (BufferedReader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                String[] t = line.split(";", -1);
                if (t.length < 5) continue;
                Professor p = new Professor(t[0].trim(), t[1].trim(), t[2].trim(), t[3].trim(), t[4].trim());
                sec.cadastrarProfessor(p);
            }
        }
    }

    private static void importarCursos(Secretaria sec) throws IOException {
        Path f = p("cursos.csv");
        if (!Files.exists(f)) return;
        try (BufferedReader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                String[] t = line.split(";", -1);
                if (t.length < 3) continue;
                Curso c = new Curso(t[0].trim(), t[1].trim(), parseIntSafe(t[2]));
                sec.cadastrarCurso(c);
            }
        }
    }

    private static void importarDisciplinas(Secretaria sec) throws IOException {
        Path f = p("disciplinas.csv");
        if (!Files.exists(f)) return;

        // mapa de cursos por código
        Map<String, Curso> mapCurso = new HashMap<>();
        for (Curso c : sec.getCursos()) {
            mapCurso.put(c.getCodigo(), c);
        }

        try (BufferedReader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                String[] t = line.split(";", -1);
                if (t.length < 3) continue;

                String codigo = t[0].trim();
                String nome = t[1].trim();
                int creditos = parseIntSafe(t[2]);
                Disciplina d = new Disciplina(codigo, nome, creditos);

                // tentar associar ao curso, se vier cursoCodigo
                Curso curso = null;
                if (t.length >= 6 && !t[5].trim().isEmpty()) {
                    curso = mapCurso.get(t[5].trim());
                }
                if (curso == null) {
                    // fallback para um curso genérico caso não exista
                    curso = mapCurso.computeIfAbsent("GEN", k -> {
                        Curso gen = new Curso("GEN", "Geral", 0);
                        sec.cadastrarCurso(gen);
                        return gen;
                    });
                }
                sec.cadastrarDisciplina(d, curso);
            }
        }
    }

    private static void importarCurriculos(Secretaria sec) throws IOException {
        Path f = p("curriculos.csv");
        if (!Files.exists(f)) return;

        // mapas
        Map<String, Curso> mapCurso = new HashMap<>();
        for (Curso c : sec.getCursos()) {
            mapCurso.put(c.getCodigo(), c);
        }
        Map<String, Disciplina> mapDisc = new HashMap<>();
        for (Disciplina d : sec.getDisciplinas()) {
            mapDisc.put(d.getCodigo(), d);
        }

        try (BufferedReader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                String[] t = line.split(";", -1);
                if (t.length < 4) continue;

                String cursoCod = t[0].trim();
                int ano = parseIntSafe(t[1]);
                int sem = parseIntSafe(t[2]);

                Curso curso = mapCurso.get(cursoCod);
                Curriculo c;
                if (curso != null) {
                    c = sec.gerarCurriculo(curso, ano, sem);
                } else {
                    // se não achar o curso, cria currículo sem curso atrelado (evita crash)
                    c = sec.gerarCurriculo(ano, sem);
                }

                if (!t[3].trim().isEmpty()) {
                    String[] cods = t[3].split(",");
                    for (String cod : cods) {
                        Disciplina d = mapDisc.get(cod.trim());
                        if (d != null) {
                            try {
                                c.adicionarDisciplina(d);
                            } catch (Exception ex) {
                                // Se der conflito de curso, apenas ignora aquela disciplina
                                System.out.println("Aviso: " + ex.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    /* ========================= UTILS ========================= */

    private static String s(String x){
        return x == null ? "" : x.replace(";", ",").trim();
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}
