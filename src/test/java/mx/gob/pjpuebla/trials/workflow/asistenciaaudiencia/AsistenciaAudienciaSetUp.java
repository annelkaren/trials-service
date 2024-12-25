package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionSetUp;
import mx.gob.pjpuebla.trials.core.salas.SalaSetUp;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaSetUp;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Asistencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

import java.time.LocalDateTime;

public class AsistenciaAudienciaSetUp {

    public AsistenciaAudienciaSetUp(){

    }

    public static AsistenciaAudiencia asistenciaAudiencia(){
        AsistenciaAudiencia asistenciaAudiencia = new AsistenciaAudiencia()
                .setId(1)
                .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentos())
                .setAudiencia(AudienciaSetUp.generarAudiencia(LocalDateTime.now(), SalaSetUp.createSala(), BloqueSetUp.createBloque(), TipoAudienciaSetUp.createTipoAudencia(), CarpetaSetUp.create()))
                .setAsistencia(Asistencia.SI)
                .setDocumentoIdentificacion(DocumentoIdentificacionSetUp.createDocIdentificacion())
                .setUrlDocumento("URL");
        asistenciaAudiencia.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        return asistenciaAudiencia;
    }

    public static AsistenciaAudienciaResponse asistenciaAudienciaResponse() {
        return new AsistenciaAudienciaResponse(1, 53, AudienciaSetUp.createAudienciasResponseRecord(), Asistencia.SI, "Visa", "URL");
    }
}
