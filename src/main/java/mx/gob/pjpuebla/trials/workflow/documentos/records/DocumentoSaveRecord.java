package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;

import java.io.Serializable;
import java.util.List;

public record DocumentoSaveRecord(

        PersonaDocumentoItemRecord actor,
        PersonaDocumentoItemRecord demandado,
        String ultimoDomicilioFamiliar,
        String domicilioAcreedor,
        List<String> anexos,
        Integer tipoJuicioId,
        DocumentoData general) implements Serializable {
}
