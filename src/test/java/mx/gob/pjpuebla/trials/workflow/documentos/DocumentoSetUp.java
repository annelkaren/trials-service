package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DocumentoSetUp {
    private DocumentoSetUp() {
    }

    public static Documento create(TipoDocumento tipoDocumento, TipoJuicio tipoJuicio) {
        Carpeta carpeta = new Carpeta()
                .setId(1)
                .setVersion(1)
                .setFolio("1")
                .setExpediente("000001/2024")
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setTipoJuicio(tipoJuicio)
                .setSelloEstatus(SelloEstatus.VALIDO);
        Documento documento = new Documento()
                .setId(1)
                .setVersion(1)
                .setTipoDocumento(tipoDocumento)
                .setCarpeta(carpeta);
        documento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return documento;
    }

    public static DocumentoSaveRecord createDocumentoSaveRecord(Integer tipoJuicio) {
        List<String> anexos = Arrays.asList("Anexo 1", "Anexo 2", "Anexo3");
        return new DocumentoSaveRecord(
                PersonasDocumentosSetUp.createPersonaDocumentoItemRecord(),
                PersonasDocumentosSetUp.createPersonaDocumentoItemRecord(),
                anexos,
                tipoJuicio);
    }
}
