package mx.gob.pjpuebla.trials.workflow.visitaduria;

import java.io.Serializable;

public record BandejaTurnoRecord(
        Integer id,
        Integer carpetaId,
        String expediente,
        String fecha,
        String hora,
        String estado,
        String vencimiento,
        // String fechaCancelado,
        String nombreUsuario,
        Integer dias,
        String entrada,
        String fechaCancelado
)implements Serializable {
}
