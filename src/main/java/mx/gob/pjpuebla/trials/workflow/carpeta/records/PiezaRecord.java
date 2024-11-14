package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.util.ArrayList;
import java.util.List;

public record PiezaRecord(
    Integer tipoPiezaId,
    String clavePieza,
    List<Integer> documentos
) {
    public PiezaRecord{
        documentos = new ArrayList<>(documentos);
    }
}
