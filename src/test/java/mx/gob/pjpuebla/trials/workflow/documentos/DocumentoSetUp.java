package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DocumentoSetUp {
    private DocumentoSetUp() {
    }

    public static Documento create(TipoJuicio tipoJuicio) {
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
                .setCarpeta(carpeta);
        documento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return documento;
    }

    public static DocumentoSaveRecord createDocumentoSaveRecord(Integer tipoJuicio) {
        List<String> anexos = Arrays.asList("Anexo 1", "Anexo 2", "Anexo3");
        DocumentoData docData = new DocumentoData();

        return new DocumentoSaveRecord(
                PersonasDocumentosSetUp.createPersonaDocumentoItemRecord(),
                PersonasDocumentosSetUp.createPersonaDocumentoItemRecord(),
                anexos,
                tipoJuicio,
                docData);
    }

    public static BandejaRecepcionRecord createBandejaRecepcion(){
        List<Anexo> anexos =  Collections.singletonList(AnexoSetUp.createAnexo());
        return new BandejaRecepcionRecord("1", "00001", TipoCarpeta.DEMANDA, "ruta/carpeta", anexos);
    }
}
