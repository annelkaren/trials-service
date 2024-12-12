package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.Prioridad;

import java.io.Serializable;

public record AsignadoTurnadoRecord(
        Integer idDocumentoAsignado,
        Integer idPersonalJuzgado,
        Integer idConcepto,
        Integer horas,
        Prioridad prioridad
) implements Serializable {
}
