package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;

import java.io.Serializable;
import java.util.List;

public record DocumentoResponseRecord(
        PersonaDocumentoRecord actor,
        PersonaDocumentoRecord demandado,
        List<String> anexos,
        String tipoJuicio
) implements Serializable {
}
