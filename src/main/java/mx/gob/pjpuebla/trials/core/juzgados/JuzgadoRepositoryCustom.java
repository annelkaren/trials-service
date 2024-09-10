package mx.gob.pjpuebla.trials.core.juzgados;

import java.time.LocalDate;

public interface JuzgadoRepositoryCustom {
    public String generarSecuenciaExpediente(Integer juzgadoId);
    public Boolean eliminarSecuenciaExpediente(Integer juzgadoId);
    public String getNumeroExpediente(Integer juzgadoId);
    public Boolean reiniciarSecuenciasExpedientes();
    public Boolean revisarSecuencia(Integer juzgadoId, LocalDate date);
}
