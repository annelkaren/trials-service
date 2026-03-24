package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.util.List;

public record DocumentoImpresionMasivaRequestRecord(
        List<Integer> documentoIds
) {
}
