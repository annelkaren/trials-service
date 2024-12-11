package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;

public record DigitalizacionRecord(
    Integer documentoId,
    String rutaArchivo,
    String nombreArchivo
) implements Serializable
 {

}
