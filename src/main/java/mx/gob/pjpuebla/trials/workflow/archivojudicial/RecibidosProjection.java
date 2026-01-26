package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDateTime;

public interface RecibidosProjection {

    Long getId();

    String getJuzgado();

    String getExpediente();

    String getTipo();

    Integer getTipoId();

    LocalDateTime getFechaRecepcion();

    String getActorPrincipal();

    String getDemandadoPrincipal();

    Integer getPaqueteId();
}
