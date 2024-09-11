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

    public static Documento create(TipoDocumento tipoDocumento, TipoJuicio tipoJuicio, Juzgado juzgado){
        Documento documento = new Documento()
        .setExpediente("000001")
        .setFolio(null)
        .setJuzgado(juzgado)
        .setTipoDocumento(tipoDocumento)
        .setTipoJuicio(tipoJuicio)
        .setStatus("Recepcion");
        return documento;
    }
}
