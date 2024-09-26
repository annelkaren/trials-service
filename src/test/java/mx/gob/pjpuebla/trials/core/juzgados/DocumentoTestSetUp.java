package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;


public class DocumentoTestSetUp {
    private DocumentoTestSetUp() {
    }

    public static Documento create() {
        Carpeta carpeta = new Carpeta()
                .setId(1)
                .setExpediente("000001/2024");
        return new Documento()
                .setId(1)
                .setCarpeta(carpeta);
    }

}
