package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.documentos.Documento;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.TipoDocumento;


public class DocumentoTestSetUp {
    private DocumentoTestSetUp(){}

    public static Documento create(TipoDocumento tipoDocumento, TipoJuicio tipoJuicio){
        Documento documento = new Documento()
        .setExpediente(null)
        .setFolio(null)
        .setJuzgado(null)
        .setTipoDocumento(tipoDocumento)
        .setTipoJuicio(tipoJuicio)
        .setStatus("Recepcion");

        return documento;
    }
}
