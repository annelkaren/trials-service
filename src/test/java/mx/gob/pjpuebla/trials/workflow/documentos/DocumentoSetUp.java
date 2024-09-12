package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.TipoDocumento;


public class DocumentoSetUp {
    private DocumentoSetUp() {
    }

    public static Documento create(TipoDocumento tipoDocumento, TipoJuicio tipoJuicio) {
        return new Documento()
                .setExpediente(null)
                .setFolio(null)
                .setJuzgado(null)
                .setTipoDocumento(tipoDocumento)
                .setTipoJuicio(tipoJuicio)
                .setEstatusProcesal("Recepcion");
    }
}
