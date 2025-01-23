package mx.gob.pjpuebla.trials.litigante.responselitigante;

import java.io.Serializable;
import java.util.List;

public record ExpedienteAutorizadoRecord(
        List<AcuerdoSentenciaRecord> expedienteAutorizado
) implements Serializable {
}