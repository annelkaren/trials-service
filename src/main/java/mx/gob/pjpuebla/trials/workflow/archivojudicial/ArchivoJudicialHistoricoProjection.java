package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDateTime;

public interface ArchivoJudicialHistoricoProjection {

    Integer getId();

    String getTipoEntidad();

    String getFolio();

    String getExpediente();

    String getEstadoMovimiento();

    String getMateria();

    Integer getTipoId();

    LocalDateTime getFechaHora();
}