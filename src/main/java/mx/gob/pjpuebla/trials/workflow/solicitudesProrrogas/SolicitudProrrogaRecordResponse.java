package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.time.LocalDate;
import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

public record SolicitudProrrogaRecordResponse(
    Integer id,
    String expediente,
    String tipoEntrada,
    String motivo,
    LocalDateTime fechaAsignacion,
    LocalDate fechaCalculada,
    LocalDate fechaProrroga,
    EstadoCarpeta estadoCarpeta,
    String nombreCompleto
) {
    public SolicitudProrrogaRecordResponse(
        Integer id,
        String expediente,
        TipoCarpeta tipoCarpeta,
        String motivo,
        LocalDateTime fechaAsignacion,
        Integer dias,
        LocalDate fechaProrroga,
        EstadoCarpeta estadoCarpeta,
        String nombreCompleto
    ) {
       
        this(
            id,
            expediente,
            tipoCarpeta != null ? tipoCarpeta.getEtiqueta() : null,,
            motivo,
            fechaAsignacion,
            (fechaAsignacion != null && dias != null)
                ? fechaAsignacion.plusDays(dias).toLocalDate()
                : null,
            fechaProrroga,
            estadoCarpeta,
            nombreCompleto
        );
    }
}

