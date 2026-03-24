package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import mx.gob.pjpuebla.trials.util.enums.carpeta.Urgente;

import java.time.LocalDateTime;

public record SolicitudesRecord(Long id,
                                String juzgado,
                                String expediente,
                                String actor,
                                String demandado,
                                String tipo,
                                LocalDateTime fechaSolicitud,
                                Urgente urgente,
                                String fechaTermino,
                                String padre) {
}
