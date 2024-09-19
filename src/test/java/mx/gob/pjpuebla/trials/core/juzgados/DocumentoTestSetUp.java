package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;


public class DocumentoTestSetUp {
    private DocumentoTestSetUp() {
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

    public static Documento create(TipoDocumento tipoDocumento, TipoJuicio tipoJuicio, Juzgado juzgado) {
        return new Documento()
                .setId(1)
                .setExpediente("000001/2024")
                .setFolio(null)
                .setJuzgado(juzgado)
                .setTipoDocumento(tipoDocumento)
                .setTipoJuicio(tipoJuicio)
                .setEstatusProcesal("Recepcion");
    }
}
