import java.util.List;

public class SistemaCobrancas implements ISistemaCobrancas {

    @Override
    public void notificarInscricao(Aluno a, Curriculo c, List<Disciplina> disciplinas) {
        System.out.println("=== NOTIFICACAO DE COBRANCA ===");
        System.out.println("Aluno: " + a.getNome() + " (" + a.getMatriculaAluno() + ")");
        System.out.println("Curriculo: " + c.getAno() + "/" + c.getSemestre());
        System.out.println("Disciplinas matriculadas:");

        int totalCreditos = 0;
        for (Disciplina d : disciplinas) {
            System.out.println("- " + d.getNome() + " (" + d.getCreditos() + " creditos)");
            totalCreditos += d.getCreditos();
        }

        System.out.println("Total de creditos: " + totalCreditos);
        System.out.println("Valor a ser cobrado: R$ " + (totalCreditos * 50.0));
        System.out.println("================================");
    }
}
