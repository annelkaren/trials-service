package mx.gob.pjpuebla.trials.workflow.documentos.amparos;

import java.time.LocalDateTime;

public record AmparoRecordResponse(    
    Integer piezaId,
    Integer documentoId,
    String numeroPieza,
    LocalDateTime fechaAsignacion) {

}
