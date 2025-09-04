import java.util.List;

public class SistemaCobrancas implements ISistemaCobrancas {
    
    @Override
    public void notificarInscricao(Aluno a, Curriculo c, List<Disciplina> disciplinas) {
        System.out.println("=== NOTIFICAÇÃO DE COBRANÇA ===");
        System.out.println("Aluno: " + a.getNome() + " (" + a.getMatriculaAluno() + ")");
        System.out.println("Currículo: " + c.getAno() + "/" + c.getSemestre());
        System.out.println("Disciplinas matriculadas:");
        
        int totalCreditos = 0;
        for (Disciplina disciplina : disciplinas) {
            System.out.println("- " + disciplina.getNome() + " (" + disciplina.getCreditos() + " créditos)");
            totalCreditos += disciplina.getCreditos();
        }
        
        System.out.println("Total de créditos: " + totalCreditos);
        System.out.println("Valor a ser cobrado: R$ " + (totalCreditos * 50.0)); // R$ 50 por crédito
        System.out.println("================================");
    }
}
