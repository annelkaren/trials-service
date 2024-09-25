package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDTO;

import java.io.Serializable;
import java.util.List;

public record DocumentoResponseRecord(
        PersonaDocumentoDTO actor,
        PersonaDocumentoDTO demandado,
        List<String> anexos
) implements Serializable {
}
