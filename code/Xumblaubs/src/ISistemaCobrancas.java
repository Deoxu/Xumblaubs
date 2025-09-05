import java.util.List;

public interface ISistemaCobrancas {
    void notificarInscricao(Aluno a, Curriculo c, List<Disciplina> disciplinas);
}
