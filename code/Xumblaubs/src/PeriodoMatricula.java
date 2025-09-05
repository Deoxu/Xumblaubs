import java.util.Date;
import java.util.Objects;

public class PeriodoMatricula {
    private final Date dataInicio;
    private final Date dataFim;
    private final TipoPeriodo tipo; // MATRICULA ou CANCELAMENTO

    public PeriodoMatricula(Date dataInicio, Date dataFim, TipoPeriodo tipo) {
        Objects.requireNonNull(dataInicio, "dataInicio não pode ser nula");
        Objects.requireNonNull(dataFim, "dataFim não pode ser nula");
        Objects.requireNonNull(tipo, "tipo não pode ser nulo");
        if (!dataInicio.before(dataFim)) {
            throw new IllegalArgumentException("dataInicio deve ser anterior a dataFim");
        }
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipo = tipo;
    }

    public boolean estaAtivo() {
        Date agora = new Date();
        // inclusivo nas bordas
        return !agora.before(dataInicio) && !agora.after(dataFim);
    }

    public boolean podeMatricular() {
        return tipo == TipoPeriodo.MATRICULA && estaAtivo();
    }

    public boolean podeCancelar() {
        return tipo == TipoPeriodo.CANCELAMENTO && estaAtivo();
    }

    // Getters
    public Date getDataInicio() { return dataInicio; }
    public Date getDataFim() { return dataFim; }
    public TipoPeriodo getTipo() { return tipo; }
}
