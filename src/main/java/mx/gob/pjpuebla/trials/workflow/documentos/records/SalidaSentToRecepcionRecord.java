package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.util.List;

public record SalidaSentToRecepcionRecord(
        List<Integer> idList,
        Integer personaCarrito
) implements Serializable {
}
