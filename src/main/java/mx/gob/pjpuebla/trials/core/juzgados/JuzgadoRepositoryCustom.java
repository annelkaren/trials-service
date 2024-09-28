package mx.gob.pjpuebla.trials.core.juzgados;

import java.time.LocalDate;

public interface JuzgadoRepositoryCustom {
    String generarSecuenciaExpediente(Integer juzgadoId);

    Boolean eliminarSecuenciaExpediente(Integer juzgadoId);

    String getNumeroExpediente(Integer juzgadoId);

    Boolean reiniciarSecuenciasExpedientes();

    Boolean revisarSecuencia(Integer juzgadoId, LocalDate date);
}
