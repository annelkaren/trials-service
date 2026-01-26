package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import mx.gob.pjpuebla.trials.util.enums.carpeta.Urgente;

import java.time.LocalDateTime;

public interface SolicitudesProjection {

    Long getId();

    String getJuzgado();

    String getExpediente();

    String getTipo();

    Integer getTipoId();

    LocalDateTime getFechaSolicitud();

    String getActorPrincipal();

    String getDemandadoPrincipal();

    Urgente getUrgente();
}
