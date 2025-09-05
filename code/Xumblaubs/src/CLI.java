import java.util.*;

public class CLI {
    private final Scanner sc = new Scanner(System.in);

    private final Secretaria secretaria;
    private final ISistemaCobrancas cobrancas;

    public CLI(Secretaria secretaria, ISistemaCobrancas cobrancas) {
        this.secretaria = secretaria;
        this.cobrancas = cobrancas;
    }

    // --- helper: normaliza string para ASCII (remove acentos/Unicode) ---
    private static String ascii(String s) {
        if (s == null)
            return "";
        String norm = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        norm = norm.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return norm.replace('–', '-').replace('—', '-');
    }

    public void start() {
        while (true) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Login Aluno");
            System.out.println("2) Login Professor");
            System.out.println("3) Login Secretaria");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    loginAluno();
                    break;
                case "2":
                    loginProfessor();
                    break;
                case "3":
                    loginSecretaria();
                    break;
                case "0":
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void loginAluno() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Senha: ");
        String senha = sc.nextLine().trim();

        Aluno logado = secretaria.getAlunos().stream()
                .filter(a -> a.autenticar(email, senha))
                .findFirst().orElse(null);

        if (logado == null) {
            System.out.println("Credenciais invalidas.");
            return;
        }
        menuAluno(logado);
    }

    private void menuAluno(Aluno aluno) {
        while (true) {
            System.out.println("\n=== MENU ALUNO (" + ascii(aluno.getNome()) + ") ===");
            System.out.println("1) Listar disciplinas de um curriculo");
            System.out.println("2) Matricular-se");
            System.out.println("3) Cancelar matricula");
            System.out.println("4) Minhas matriculas (por curriculo)");
            System.out.println("5) Finalizar inscricao e notificar cobranca (por curriculo)");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    listarDisciplinasDeCurriculo();
                    break;
                case "2":
                    acaoMatricular(aluno);
                    break;
                case "3":
                    acaoCancelar(aluno);
                    break;
                case "4":
                    listarMinhasMatriculas(aluno);
                    break;
                case "5":
                    finalizarInscricaoENotificar(aluno);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void listarDisciplinasDeCurriculo() {
        Curriculo c = escolherCurriculo();
        if (c == null)
            return;

        String cursoLabel = (c.getCurso() != null)
                ? (c.getCurso().getCodigo() + " - " + ascii(c.getCurso().getNome()))
                : "(sem curso)";

        System.out.println("\nDisciplinas de " + cursoLabel + " - " + c.getAno() + "/" + c.getSemestre() + ":");
        for (Disciplina d : c.getDisciplinas()) {
            System.out.println("- " + d.getCodigo() + " | " + ascii(d.getNome())
                    + " | creditos: " + d.getCreditos()
                    + " | ativa: " + d.estaAtiva()
                    + " | vagas: " + d.vagasDisponiveis());
        }
    }

    private void acaoMatricular(Aluno aluno) {
        if (!secretaria.podeMatricular()) {
            System.out.println("Fora do periodo de matricula.");
            return;
        }
        Curriculo c = escolherCurriculo();
        if (c == null)
            return;

        System.out.print("Codigo da disciplina: ");
        String cod = sc.nextLine().trim();
        Disciplina d = secretaria.getDisciplinas().stream()
                .filter(x -> cod.equalsIgnoreCase(x.getCodigo()))
                .findFirst().orElse(null);
        if (d == null) {
            System.out.println("Disciplina nao encontrada.");
            return;
        }

        System.out.print("Tipo (OBRIGATORIA/OPTATIVA): ");
        String tipoStr = sc.nextLine().trim().toUpperCase();
        TipoDisciplina tipo;
        try {
            tipo = TipoDisciplina.valueOf(tipoStr);
        } catch (Exception e) {
            System.out.println("Tipo invalido.");
            return;
        }

        try {
            aluno.matricular(d, c, tipo, secretaria);
            System.out.println("Matricula realizada.");
        } catch (Exception e) {
            System.out.println("Falha: " + ascii(e.getMessage()));
        }
    }

    private void acaoCancelar(Aluno aluno) {
        if (!secretaria.podeCancelar()) {
            System.out.println("Fora do periodo de cancelamento.");
            return;
        }
        Curriculo c = escolherCurriculo();
        if (c == null)
            return;

        List<Matricula> mats = aluno.obterMatriculas(c);
        if (mats.isEmpty()) {
            System.out.println("Sem matriculas ativas nesse curriculo.");
            return;
        }
        for (int i = 0; i < mats.size(); i++) {
            Matricula m = mats.get(i);
            System.out.println((i + 1) + ") " + m.getDisciplina().getCodigo() + " - "
                    + ascii(m.getDisciplina().getNome()) + " (" + m.getTipo() + ")");
        }
        System.out.print("Escolha # para cancelar: ");
        int idx = parseIntSafe(sc.nextLine()) - 1;
        if (idx < 0 || idx >= mats.size()) {
            System.out.println("Invalido.");
            return;
        }
        try {
            aluno.cancelar(mats.get(idx), secretaria);
            System.out.println("Cancelada.");
        } catch (Exception e) {
            System.out.println("Falha: " + ascii(e.getMessage()));
        }
    }

    private void listarMinhasMatriculas(Aluno aluno) {
        Curriculo c = escolherCurriculo();
        if (c == null)
            return;

        List<Matricula> mats = aluno.obterMatriculas(c);
        if (mats.isEmpty()) {
            System.out.println("Sem matriculas ativas nesse curriculo.");
            return;
        }
        for (Matricula m : mats) {
            System.out.println("- " + m.getDisciplina().getCodigo() + " | "
                    + ascii(m.getDisciplina().getNome()) + " | " + m.getTipo());
        }
    }

    private void finalizarInscricaoENotificar(Aluno aluno) {
        Curriculo c = escolherCurriculo();
        if (c == null)
            return;
        secretaria.notificarCobrancaDoSemestre(aluno, c, cobrancas);
        System.out.println("Cobranca consolidada (idempotente) para " + ascii(aluno.getNome())
                + " em " + c.getAno() + "/" + c.getSemestre());
    }

    private void loginProfessor() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Senha: ");
        String senha = sc.nextLine().trim();

        Professor prof = secretaria.getProfessores().stream()
                .filter(p -> p.autenticar(email, senha))
                .findFirst().orElse(null);

        if (prof == null) {
            System.out.println("Credenciais invalidas.");
            return;
        }
        menuProfessor(prof);
    }

    private void menuProfessor(Professor p) {
        while (true) {
            System.out.println("\n=== MENU PROFESSOR (" + ascii(p.getNome()) + ") ===");
            System.out.println("1) Listar alunos de uma disciplina em um curriculo");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    Curriculo c = escolherCurriculo();
                    if (c == null)
                        break;
                    System.out.print("Codigo da disciplina: ");
                    String cod = sc.nextLine().trim();
                    Disciplina d = secretaria.getDisciplinas().stream()
                            .filter(x -> cod.equalsIgnoreCase(x.getCodigo()))
                            .findFirst().orElse(null);
                    if (d == null) {
                        System.out.println("Disciplina nao encontrada.");
                        break;
                    }
                    List<Aluno> alunos = p.listarAlunos(d, c);
                    if (alunos.isEmpty()) {
                        System.out.println("Sem alunos ativos nessa disciplina/curriculo.");
                    } else {
                        alunos.forEach(a -> System.out.println("- " + a.getMatriculaAluno()
                                + " | " + ascii(a.getNome())));
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void loginSecretaria() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Senha: ");
        String senha = sc.nextLine().trim();

        if (!secretaria.autenticar(email, senha)) {
            System.out.println("Credenciais invalidas.");
            return;
        }
        menuSecretaria();
    }

    private void menuSecretaria() {
        while (true) {
            System.out.println("\n=== MENU SECRETARIA ===");
            System.out.println("1) Gerar curriculo");
            System.out.println("2) Cadastrar aluno");
            System.out.println("3) Cadastrar professor");
            System.out.println("4) Cadastrar disciplina em curso");
            System.out.println("5) Definir periodo (MATRICULA/CANCELAMENTO)");
            System.out.println("6) Encerrar periodo para um curriculo");
            System.out.println("7) Exportar CSVs");
            System.out.println("8) Importar CSVs (parcial)");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    System.out.print("Ano: ");
                    int ano = parseIntSafe(sc.nextLine());
                    System.out.print("Semestre: ");
                    int sem = parseIntSafe(sc.nextLine());
                    secretaria.gerarCurriculo(ano, sem);
                    break;
                case "2":
                    System.out.print("id: ");
                    String idA = sc.nextLine();
                    System.out.print("nome: ");
                    String nmA = ascii(sc.nextLine());
                    System.out.print("email: ");
                    String emA = sc.nextLine();
                    System.out.print("senha: ");
                    String seA = sc.nextLine();
                    System.out.print("matricula: ");
                    String ra = sc.nextLine();
                    secretaria.cadastrarAluno(new Aluno(idA, nmA, emA, seA, ra));
                    break;
                case "3":
                    System.out.print("id: ");
                    String idP = sc.nextLine();
                    System.out.print("nome: ");
                    String nmP = ascii(sc.nextLine());
                    System.out.print("email: ");
                    String emP = sc.nextLine();
                    System.out.print("senha: ");
                    String seP = sc.nextLine();
                    System.out.print("idProfessor: ");
                    String sip = sc.nextLine();
                    secretaria.cadastrarProfessor(new Professor(idP, nmP, emP, seP, sip));
                    break;
                case "4": {
                    System.out.print("Curso (codigo, nome, creditosTotais): ");
                    String[] parts = sc.nextLine().split(",");
                    if (parts.length < 3) {
                        System.out.println("Entrada invalida.");
                        break;
                    }
                    String codCurso = parts[0].trim();
                    String nomeCurso = parts[1].trim();
                    int credTotais = parseIntSafe(parts[2].trim());

                    // Busca curso pelo codigo; se nao existir, cadastra
                    Curso curso = secretaria.getCursos().stream()
                            .filter(cu -> codCurso.equalsIgnoreCase(cu.getCodigo()))
                            .findFirst()
                            .orElseGet(() -> {
                                Curso novo = new Curso(codCurso, nomeCurso, credTotais);
                                secretaria.cadastrarCurso(novo);
                                return novo;
                            });

                    System.out.print("Disciplina (codigo, nome, creditos): ");
                    String[] dparts = sc.nextLine().split(",");
                    if (dparts.length < 3) {
                        System.out.println("Entrada invalida.");
                        break;
                    }
                    Disciplina d = new Disciplina(dparts[0].trim(), dparts[1].trim(), parseIntSafe(dparts[2].trim()));
                    secretaria.cadastrarDisciplina(d, curso);

                    // Oferecer para adicionar a um curriculo DO MESMO CURSO
                    List<Curriculo> currsDoCurso = new ArrayList<>();
                    for (Curriculo cx : secretaria.getCurriculos()) {
                        if (cx.getCurso() != null && cx.getCurso().equals(curso)) {
                            currsDoCurso.add(cx);
                        }
                    }

                    if (currsDoCurso.isEmpty()) {
                        System.out.println(
                                "Nao ha curriculo do curso " + curso.getNome() + ". Use 'Gerar curriculo' primeiro.");
                        break;
                    }

                    System.out.println("Adicionar a qual curriculo do curso " + curso.getNome() + "?");
                    for (int i = 0; i < currsDoCurso.size(); i++) {
                        Curriculo cx = currsDoCurso.get(i);
                        System.out.printf("%d) %d/%d%n", (i + 1), cx.getAno(), cx.getSemestre());
                    }
                    System.out.print("Escolha (ou 0 para nao adicionar): ");
                    int idx = parseIntSafe(sc.nextLine()) - 1;
                    if (idx >= 0 && idx < currsDoCurso.size()) {
                        try {
                            currsDoCurso.get(idx).adicionarDisciplina(d);
                            System.out.println("Disciplina adicionada ao curriculo " +
                                    currsDoCurso.get(idx).getAno() + "/" + currsDoCurso.get(idx).getSemestre());
                        } catch (Exception ex) {
                            System.out.println("Aviso: " + ex.getMessage());
                        }
                    } else {
                        System.out.println("Disciplina cadastrada no curso, sem adicionar a curriculo.");
                    }
                    break;
                }

                case "5": {
                    System.out.println("Definir periodo (aceita: millis | vazio=padrao | AAAA-MM-DD | +Nd | 'agora')");
                    System.out.print("Data inicio: ");
                    String iniStr = sc.nextLine();
                    long ini = millisFrom(iniStr); // vazio/now/+Nd/AAAA-MM-DD/numero

                    System.out.print("Data fim: ");
                    String fimStr = sc.nextLine();
                    long fim = fimStr.trim().isEmpty() ? millisFrom("+7d") : millisFrom(fimStr);

                    System.out.print("Tipo (MATRICULA/CANCELAMENTO): ");
                    String tp = sc.nextLine().trim().toUpperCase();

                    if (ini <= 0 || fim <= 0 || fim <= ini) {
                        System.out.println("Datas invalidas.");
                        break;
                    }

                    try {
                        System.out.println("Inicio resolvido: " + new Date(ini));
                        System.out.println("Fim resolvido   : " + new Date(fim));
                        secretaria.definirPeriodoMatricula(new Date(ini), new Date(fim), TipoPeriodo.valueOf(tp));
                        System.out.println("Periodo definido com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Tipo invalido.");
                    }
                    break;
                }

                case "6":
                    Curriculo c = escolherCurriculo();
                    if (c != null)
                        secretaria.encerrarPeriodoMatricula(c);
                    break;
                case "7":
                    FileStore.exportarCSV(secretaria);
                    System.out.println("Arquivos CSV exportados na pasta ./data");
                    break;
                case "8":
                    FileStore.importarCSV(secretaria);
                    System.out.println("Importacao basica concluida.");
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    // Converte entradas amigaveis em millis:
    // "" / "agora" / "now" -> agora
    // "+Nd" -> agora + N dias
    // "AAAA-MM-DD" -> meia-noite desse dia
    // numero -> usa como millis
    private static long millisFrom(String s) {
        if (s == null)
            return -1L;
        String x = s.trim();
        if (x.isEmpty() || x.equalsIgnoreCase("agora") || x.equalsIgnoreCase("now")) {
            return System.currentTimeMillis();
        }
        if (x.matches("^\\+?\\d+[dD]$")) {
            int days = Integer.parseInt(x.replaceAll("[^0-9]", ""));
            return System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L;
        }
        if (x.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            String[] parts = x.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]); // 1..12
            int day = Integer.parseInt(parts[2]);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.YEAR, year);
            cal.set(java.util.Calendar.MONTH, month - 1); // Calendar: 0..11
            cal.set(java.util.Calendar.DAY_OF_MONTH, day);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        }
        try {
            return Long.parseLong(x);
        } catch (Exception e) {
            return -1L;
        }
    }

    private Curriculo escolherCurriculo() {
        List<Curriculo> list = secretaria.getCurriculos();
        if (list.isEmpty()) {
            System.out.println("Nenhum curriculo cadastrado.");
            return null;
        }

        System.out.println("\nSelecione um curriculo:");
        for (int i = 0; i < list.size(); i++) {
            Curriculo c = list.get(i);
            String cursoLabel = (c.getCurso() != null)
                    ? (c.getCurso().getCodigo() + " - " + ascii(c.getCurso().getNome()))
                    : "(sem curso)";
            int qtd = c.getDisciplinas() != null ? c.getDisciplinas().size() : 0;
            System.out.printf("%d) %s - %d/%d [%d disciplinas]%n",
                    (i + 1), cursoLabel, c.getAno(), c.getSemestre(), qtd);
        }

        System.out.print("Escolha: ");
        int idx = parseIntSafe(sc.nextLine()) - 1;
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Opcao invalida.");
            return null;
        }
        return list.get(idx);
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }

}
