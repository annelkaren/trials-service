package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDate;

public interface ArchivoJudicialProjection {

    Long getId();

    String getJuzgado();

    String getTipo();

    Integer getTipoId();

    String getExpediente();

    LocalDate getFechaAlta();

    String getAnexos();

    Integer getPaqueteId();
}