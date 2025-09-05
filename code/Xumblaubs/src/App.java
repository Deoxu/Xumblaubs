import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTÃO ACADÊMICA ===\n");

        // Criando uma instância da Secretaria
        Secretaria secretaria = new Secretaria("SEC001", "Maria Silva", "maria@universidade.edu", "senha123");
        System.out.println("Secretaria criada: " + secretaria.getNome() + "\n");

        // 1. Gerando um currículo
        System.out.println("1. GERANDO CURRÍCULO:");
        Curriculo curriculo2024_1 = secretaria.gerarCurriculo(2024, 1);
        System.out
                .println("Currículo criado: " + curriculo2024_1.getAno() + "/" + curriculo2024_1.getSemestre() + "\n");

        // 2. Criando curso e disciplinas
        System.out.println("2. CRIANDO CURSO E DISCIPLINAS:");
        Curso cursoEngenharia = new Curso("ENG001", "Engenharia de Software", 240);

        Disciplina disciplina1 = new Disciplina("MAT001", "Cálculo I", 4);
        Disciplina disciplina2 = new Disciplina("FIS001", "Física I", 4);
        Disciplina disciplina3 = new Disciplina("PROG001", "Programação I", 6);

        secretaria.cadastrarDisciplina(disciplina1, cursoEngenharia);
        secretaria.cadastrarDisciplina(disciplina2, cursoEngenharia);
        secretaria.cadastrarDisciplina(disciplina3, cursoEngenharia);
        System.out.println();

        // 3. Criando e cadastrando professores
        System.out.println("3. CADASTRANDO PROFESSORES:");
        Professor professor1 = new Professor("PROF001", "João Santos", "joao@universidade.edu", "senha456", "PROF001");
        Professor professor2 = new Professor("PROF002", "Ana Costa", "ana@universidade.edu", "senha789", "PROF002");

        secretaria.cadastrarProfessor(professor1);
        secretaria.cadastrarProfessor(professor2);
        System.out.println();

        // 4. Criando e cadastrando alunos
        System.out.println("4. CADASTRANDO ALUNOS:");
        Aluno aluno1 = new Aluno("ALU001", "Pedro Oliveira", "pedro@estudante.edu", "senha111", "2024001");
        Aluno aluno2 = new Aluno("ALU002", "Carla Mendes", "carla@estudante.edu", "senha222", "2024002");
        Aluno aluno3 = new Aluno("ALU003", "Lucas Ferreira", "lucas@estudante.edu", "senha333", "2024003");

        secretaria.cadastrarAluno(aluno1);
        secretaria.cadastrarAluno(aluno2);
        secretaria.cadastrarAluno(aluno3);
        System.out.println();

        // 5. Adicionando disciplinas ao currículo
        System.out.println("5. ADICIONANDO DISCIPLINAS AO CURRÍCULO:");
        curriculo2024_1.adicionarDisciplina(disciplina1);
        curriculo2024_1.adicionarDisciplina(disciplina2);
        curriculo2024_1.adicionarDisciplina(disciplina3);
        System.out.println("Disciplinas adicionadas ao currículo 2024/1\n");

        // 6. Associando professores às disciplinas
        System.out.println("6. ASSOCIANDO PROFESSORES ÀS DISCIPLINAS:");
        professor1.adicionarDisciplina(disciplina1);
        professor1.adicionarDisciplina(disciplina2);
        professor2.adicionarDisciplina(disciplina3);
        System.out.println();

        // 7. Definindo períodos de matrícula
        System.out.println("7. DEFININDO PERÍODOS DE MATRÍCULA:");
        java.util.Date agora = new java.util.Date();
        java.util.Date fim = new java.util.Date(agora.getTime() + 7 * 24 * 60 * 60 * 1000); // 7 dias

        secretaria.definirPeriodoMatricula(agora, fim, TipoPeriodo.MATRICULA);
        secretaria.definirPeriodoMatricula(agora, fim, TipoPeriodo.CANCELAMENTO);
        System.out.println();

        // 8. Configurando sistema de cobrança
        System.out.println("8. CONFIGURANDO SISTEMA DE COBRANÇA:");
        SistemaCobrancas sistemaCobrancas = new SistemaCobrancas();
        Matricula.setSistemaCobrancas(sistemaCobrancas);
        System.out.println("Sistema de cobrança configurado\n");

        // 9. Demonstrando matrículas
        System.out.println("9. REALIZANDO MATRÍCULAS:");
        try {
            aluno1.matricular(disciplina1, curriculo2024_1, TipoDisciplina.OBRIGATORIA, secretaria);
            aluno1.matricular(disciplina3, curriculo2024_1, TipoDisciplina.OBRIGATORIA, secretaria);
            aluno2.matricular(disciplina1, curriculo2024_1, TipoDisciplina.OBRIGATORIA, secretaria);
            aluno3.matricular(disciplina1, curriculo2024_1, TipoDisciplina.OBRIGATORIA, secretaria);

            System.out.println("Matrículas realizadas com sucesso!");
            System.out.println("Aluno " + aluno1.getNome() + " matriculado em " + disciplina1.getNome());
            System.out.println("Aluno " + aluno1.getNome() + " matriculado em " + disciplina3.getNome());
            System.out.println("Aluno " + aluno2.getNome() + " matriculado em " + disciplina1.getNome());
            System.out.println("Aluno " + aluno3.getNome() + " matriculado em " + disciplina1.getNome());
        } catch (Exception e) {
            System.out.println("Erro na matrícula: " + e.getMessage());
        }
        System.out.println();

        // 10. Demonstrando cancelamento de matrícula
        System.out.println("10. CANCELANDO MATRÍCULA:");
        try {
            aluno1.cancelar(aluno1.getMatriculas().get(0), secretaria);
            System.out.println("Matrícula cancelada pelo aluno " + aluno1.getNome());
        } catch (Exception e) {
            System.out.println("Erro no cancelamento: " + e.getMessage());
        }
        System.out.println();

        // 11. Demonstrando funcionalidade do professor
        System.out.println("11. PROFESSOR LISTANDO ALUNOS:");
        List<Aluno> alunosDisciplina = professor1.listarAlunos(disciplina1, curriculo2024_1);
        System.out.println();

        // 12. Encerrando período de matrícula
        System.out.println("12. ENCERRANDO PERÍODO DE MATRÍCULA:");
        secretaria.encerrarPeriodoMatricula(curriculo2024_1);
        System.out.println();

        // 13. Listando informações do sistema
        System.out.println("13. RESUMO DO SISTEMA:");
        System.out.println("Total de alunos cadastrados: " + secretaria.listarAlunos().size());
        System.out.println("Total de professores cadastrados: " + secretaria.listarProfessores().size());
        System.out.println("Total de disciplinas cadastradas: " + secretaria.listarDisciplinas().size());
        System.out.println("Total de currículos gerados: " + secretaria.listarCurriculos().size());

        System.out.println("\n=== SISTEMA FINALIZADO ===");
    }
}