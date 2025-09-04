import java.util.Date;

public class PeriodoMatricula {
    private Date dataInicio;
    private Date dataFim;
    private boolean periodoAtivo;
    private String tipo; // "MATRICULA" ou "CANCELAMENTO"
    
    public PeriodoMatricula(Date dataInicio, Date dataFim, String tipo) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipo = tipo;
        this.periodoAtivo = false;
    }
    
    public boolean estaAtivo() {
        Date agora = new Date();
        return agora.after(dataInicio) && agora.before(dataFim);
    }
    
    public void ativar() {
        this.periodoAtivo = true;
    }
    
    public void desativar() {
        this.periodoAtivo = false;
    }
    
    public boolean podeMatricular() {
        return "MATRICULA".equals(tipo) && estaAtivo();
    }
    
    public boolean podeCancelar() {
        return "CANCELAMENTO".equals(tipo) && estaAtivo();
    }
    
    // Getters e Setters
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
    
    public boolean isPeriodoAtivo() {
        return periodoAtivo;
    }
    
    public void setPeriodoAtivo(boolean periodoAtivo) {
        this.periodoAtivo = periodoAtivo;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
