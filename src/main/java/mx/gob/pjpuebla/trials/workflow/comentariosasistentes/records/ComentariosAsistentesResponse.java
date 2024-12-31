package mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records;

import java.time.LocalDateTime;

public record ComentariosAsistentesResponse(
        Integer id,
        Integer idPersonaDocumento,
        String comentario,
        LocalDateTime horaRegistro
) {
}
