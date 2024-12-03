package mx.gob.pjpuebla.trials.workflow.documentos.Sentencias.records;

import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import java.time.LocalDate;
import java.util.List;



public record SentenciaRecordSave(
    Integer sentenciaId,
    Integer documentoId, 
    Integer carpetaId,
    String tipoSentencia,
    LocalDate fechaResolucion,
    String etapaProcesal,
    String tipoResolucion,
    String extractoSentencia,
    Character tamanioPapel,
    String textoEditor,
    String resumen,
    List<AcuerdoPromocionesRecord> promocionesRelacionadas
){}