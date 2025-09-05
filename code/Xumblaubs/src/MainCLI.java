public class MainCLI {
    public static void main(String[] args) {
        // Secretaria padrão
        Secretaria secretaria = new Secretaria("SEC001", "admin", "admin", "admin");

        // Professor padrão
        Professor professor = new Professor("PROF001", "prof", "prof", "prof", "prof");
        secretaria.cadastrarProfessor(professor);

        // Aluno padrão
        Aluno aluno = new Aluno("ALU001", "aluno", "aluno", "aluno", "aluno");
        secretaria.cadastrarAluno(aluno);

        ISistemaCobrancas sistemaCobrancas = new SistemaCobrancas();

        CLI cli = new CLI(secretaria, sistemaCobrancas);
        cli.start();
    }
}
