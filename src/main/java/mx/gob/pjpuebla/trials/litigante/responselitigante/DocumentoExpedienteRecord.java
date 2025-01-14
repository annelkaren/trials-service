package mx.gob.pjpuebla.trials.litigante.responselitigante;

import java.io.Serializable;

public record DocumentoExpedienteRecord(
        Integer numeroAcuerdo,
        String fechaCompletado,
        String horaCompletado,
        String rutaArchivo
) implements Serializable {
}