package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DocumentoSetUp {
    private DocumentoSetUp() {
    }

    public static Documento create(TipoDocumento tipoDocumento, TipoJuicio tipoJuicio) {
        Documento documento = new Documento()
                .setExpediente(null)
                .setFolio(null)
                .setJuzgado(null)
                .setTipoDocumento(tipoDocumento)
                .setTipoJuicio(tipoJuicio)
                .setEstatus(EstadoDocumento.CAPTURA)
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setEstatusProcesal("Recepcion");
        documento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return documento;
    }

    public static DocumentoDTO createDTO() {
        PersonaDocumentoDTO actor = new PersonaDocumentoDTO()
                .setNombre("Juan")
                .setApellidoPaterno("Perez")
                .setTipoPersona("fisica")
                .setTipoParte(1);
        PersonaDocumentoDTO demandado = new PersonaDocumentoDTO()
                .setNombre("Maria")
                .setApellidoPaterno("Sanchez")
                .setTipoPersona("fisica")
                .setTipoParte(2);
        List<String> anexos = Arrays.asList("Anexo 1", "Anexo 2", "Anexo3");
        return new DocumentoDTO().setActor(actor).setDemandado(demandado).setAnexos(anexos);
    }

}
