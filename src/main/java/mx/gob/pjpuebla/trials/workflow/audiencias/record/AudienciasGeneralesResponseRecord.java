package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record AudienciasGeneralesResponseRecord(
        Integer id,
        String tipoAudiencia,
        String juez,
        String numCarpeta,
        Integer idCarpeta,
        String lugar,
        LocalDateTime fechaHora,
        EstatusAudiencia estatus,
        Integer juzgado,
        String tipoJuicio,
        List<AsistenciaPersonaDocumento> asistenciaPersonaDocumento,
        String horaInicioAudiencia,
        String horaFinAudiencia,
        String tipoSistema,
        String materia
) implements Serializable {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    public AudienciasGeneralesResponseRecord(
            Integer id,
            String tipoAudiencia,
            String juez,
            String numCarpeta,
            Integer idCarpeta,
            String lugar,
            LocalDateTime fechaHora,
            EstatusAudiencia estatus,
            Integer juzgado,
            String tipoJuicio,
            List<AsistenciaPersonaDocumento> asistenciaPersonaDocumento,
            LocalDateTime horaInicioAudiencia,
            LocalDateTime horaFinAudiencia,
            String tipoSistema,
            String materia
    ) {
        this(
                id,
                tipoAudiencia,
                juez,
                numCarpeta,
                idCarpeta,
                lugar,
                fechaHora,
                estatus,
                juzgado,
                tipoJuicio,
                asistenciaPersonaDocumento,
                horaInicioAudiencia != null ? horaInicioAudiencia.format(FORMATTER) : null,
                horaFinAudiencia != null ? horaFinAudiencia.format(FORMATTER) : null,
                tipoSistema, 
                materia
        );
    }
}
