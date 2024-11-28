package mx.gob.pjpuebla.trials.workflow.documentos.amparos;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AmparoRecordResponse(    
    Integer piezaId,
    Integer documentoId,
    String numeroPieza,
    LocalDateTime fechaAsignacion) implements Serializable {

}
