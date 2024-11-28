package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;

import java.io.Serializable;
import java.util.List;

public record DocumentoAntiguoSaveRecord(

        PersonaDocumentoItemRecord actor,
        PersonaDocumentoItemRecord demandado,
        List<String> anexos,
        Integer tipoJuicioId,
        DocumentoData general,
        String numero,
        String anio

) implements Serializable {
}
