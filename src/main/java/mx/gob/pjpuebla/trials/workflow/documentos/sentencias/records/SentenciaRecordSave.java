package mx.gob.pjpuebla.trials.workflow.documentos.sentencias.records;

import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record SentenciaRecordSave(
        Integer sentenciaId,
        Integer documentoId,
        Integer carpetaId,
        TipoSentencia tipoSentencia,
        LocalDate fechaResolucion,
        String etapaProcesal,
        TipoResolucion tipoResolucion,
        String extractoSentencia,
        Character tamanioPapel,
        String textoEditor,
        List<AcuerdoPromocionesRecord> promocionesRelacionadas
) implements Serializable {
}