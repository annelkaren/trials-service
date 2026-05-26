package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDateTime;

public interface ArchivoJudicialProjection {

    Long getId();

    String getJuzgado();

    String getTipo();

    Integer getTipoId();

    String getExpediente();

    LocalDateTime getFechaAlta();

    String getAnexos();

    Integer getPaqueteId();
}