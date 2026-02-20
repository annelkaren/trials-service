package mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios.records;

import java.io.Serializable;

public record AnexoOficioRecord(
        Integer id,
        String nombreArchivo
) implements Serializable {
}
