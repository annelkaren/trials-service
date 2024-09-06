package mx.gob.pjpuebla.trials.core.juzgados;

public interface JuzgadoRepositoryCustom {
    public String generarSecuenciaExpediente(Integer juzgadoId);
    public Boolean eliminarSecuenciaExpediente(Integer juzgadoId);
    public String getNumeroExpediente(Integer juzgadoId);
    public Boolean reiniciarSecuenciaExpediente(Integer juzgadoId);
}
