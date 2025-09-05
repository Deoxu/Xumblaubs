import java.util.Date;

public class PeriodoMatricula {
    private Date dataInicio;
    private Date dataFim;
    private TipoPeriodo tipo; // MATRICULA ou CANCELAMENTO

    public PeriodoMatricula(Date dataInicio, Date dataFim, TipoPeriodo tipo) {
        if (dataInicio == null || dataFim == null || tipo == null || !dataFim.after(dataInicio)) {
            throw new IllegalArgumentException("Periodo invalido");
        }
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipo = tipo;
    }

    // Limites INCLUSIVOS: inicio <= agora <= fim
    public boolean estaAtivo() {
        Date agora = new Date();
        return !agora.before(dataInicio) && !agora.after(dataFim);
    }

    public boolean podeMatricular() {
        return tipo == TipoPeriodo.MATRICULA && estaAtivo();
    }

    public boolean podeCancelar() {
        return tipo == TipoPeriodo.CANCELAMENTO && estaAtivo();
    }

    // Getters/Setters
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public TipoPeriodo getTipo() {
        return tipo;
    }

    public void setTipo(TipoPeriodo tipo) {
        this.tipo = tipo;
    }
}
