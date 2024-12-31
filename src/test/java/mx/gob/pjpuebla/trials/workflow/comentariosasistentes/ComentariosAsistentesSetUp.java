package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesRecord;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesResponse;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

import java.time.LocalDateTime;

public class ComentariosAsistentesSetUp {

    public ComentariosAsistentesSetUp() {

    }

    public static ComentariosAsistentes comentariosAsistentes() {
        ComentariosAsistentes comentariosAsistentes = new ComentariosAsistentes()
                .setId(1)
                .setComentario("Comentario")
                .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentos());
        comentariosAsistentes.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        return comentariosAsistentes;
    }

    public static ComentariosAsistentesRecord comentariosAsistentesRecord() {
        return new ComentariosAsistentesRecord(1, "Comentario");
    }

    public static ComentariosAsistentesResponse comentariosAsistentesResponse() {
        return new ComentariosAsistentesResponse(1, 1, "Comentario", LocalDateTime.now());
    }
}
