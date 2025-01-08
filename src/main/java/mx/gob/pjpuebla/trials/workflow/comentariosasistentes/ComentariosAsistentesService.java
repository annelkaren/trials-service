package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesResponse;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class ComentariosAsistentesService {

    private static final String PERSONA_DOCUMENTO_NOT_FOUND = "Persona documento no encontrada";
    private static final String COMENTARIO_ASISTENTE_NOT_FOUND = "Comentario asistente no encontrado";
    private final ComentariosAsistentesRepository comentariosAsistentesRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;

    @Transactional(readOnly = true)
    public Page<ComentariosAsistentesResponse> getComentariosByPersonaDocumento(Integer personaDocumentoId, Pageable pageable) {
        return comentariosAsistentesRepository.findByPersonaDocumentoId(personaDocumentoId, pageable)
                .map(comentario -> new ComentariosAsistentesResponse(
                        comentario.getId(),
                        comentario.getPersonaDocumento().getId(),
                        comentario.getComentario(),
                        comentario.getAudit().getFechaAlta()
                ));
    }

    public ComentariosAsistentesResponse create(ComentariosAsistentesRecord comentariosAsistentes) {

        PersonaDocumento personaDocumento = personaDocumentoRepository.findById(comentariosAsistentes.idPersonaDocumento())
                .orElseThrow(() -> new IllegalArgumentException(PERSONA_DOCUMENTO_NOT_FOUND));

        ComentariosAsistentes comentario = new ComentariosAsistentes();
        comentario.setPersonaDocumento(personaDocumento);
        comentario.setComentario(comentariosAsistentes.comentario());
        comentario = comentariosAsistentesRepository.save(comentario);

        return new ComentariosAsistentesResponse(
                comentario.getId(),
                comentario.getPersonaDocumento().getId(),
                comentario.getComentario(),
                comentario.getAudit().getFechaAlta()
        );
    }

    public ComentariosAsistentesResponse update(Integer id, ComentariosAsistentesRecord comentariosAsistentes) {
        ComentariosAsistentes comentario = comentariosAsistentesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(COMENTARIO_ASISTENTE_NOT_FOUND));

        comentario.setComentario(comentariosAsistentes.comentario());
        ComentariosAsistentes updatedComentario = comentariosAsistentesRepository.save(comentario);

        return new ComentariosAsistentesResponse(
                updatedComentario.getId(),
                updatedComentario.getPersonaDocumento().getId(),
                updatedComentario.getComentario(),
                updatedComentario.getAudit().getFechaAlta()
        );
    }
}
