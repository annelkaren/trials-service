package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDTO;

import java.util.Arrays;
import java.util.List;

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
                .setEstatus(EstadoDocumento.CAPTURA)
                .setEstatusProcesal("Recepcion");
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
