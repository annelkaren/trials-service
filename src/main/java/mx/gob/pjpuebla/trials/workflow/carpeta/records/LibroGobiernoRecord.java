package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.util.enums.Migrado;

public record LibroGobiernoRecord(
        Integer id, //carpeta ID
        String numExpediente,
        LocalDateTime fechaHora,
        String tipoJuicio,
        String actor,
        String demandado,
        Boolean asignado,
        String cujus,
        EstadoMigracion estadoMigracion,
        Migrado migrado
) implements Serializable {
}
