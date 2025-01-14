package mx.gob.pjpuebla.trials.litigante.responselitigante;

import java.io.Serializable;
import java.util.List;

public record AcuerdoSentenciaRecord(
        String numeroExpediente,
        List<DocumentoExpedienteRecord> documentoExpediente
) implements Serializable {
}