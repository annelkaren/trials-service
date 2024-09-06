package mx.gob.pjpuebla.trials.core.juzgados;

public interface JuzgadoRepositoryCustom {
    public String generarSecuenciaExpediente(Integer pn_id);
    public Boolean eliminarSecuenciaExpediente(Integer pn_id);
    public String getNumeroExpediente(Integer pn_id);
    public Boolean reiniciarSecuenciaExpediente(Integer pn_id);
}
