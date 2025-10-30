package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record PiezaRecord(
    Integer tipoPiezaId,
    String clavePieza,
    Integer promocionId,
    List<Integer> documentos
) implements Serializable {
    public PiezaRecord{
        documentos = new ArrayList<>(documentos);
    }
}
