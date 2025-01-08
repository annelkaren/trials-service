package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesRecord;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesResponse;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ComentariosAsistentesServiceTest {

    @Mock
    private ComentariosAsistentesRepository comentariosAsistentesRepository;

    @Mock
    private PersonaDocumentoRepository personaDocumentoRepository;

    @InjectMocks
    private ComentariosAsistentesService comentariosAsistentesService;

    private PersonaDocumento personaDocumento;

    void setUp() {
        personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos();
    }

    @Test
    void getComentariosByPersonaDocumento_success() {
        PersonaDocumento personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos();
        personaDocumento.setId(1);

        ComentariosAsistentes comentario = ComentariosAsistentesSetUp.comentariosAsistentes();
        comentario.setPersonaDocumento(personaDocumento);

        Page<ComentariosAsistentes> page = new PageImpl<>(Collections.singletonList(comentario), PageRequest.of(0, 10), 1);

        given(comentariosAsistentesRepository.findByPersonaDocumentoId(eq(1), any(PageRequest.class)))
                .willReturn(page);

        Page<ComentariosAsistentesResponse> result = comentariosAsistentesService.getComentariosByPersonaDocumento(1, PageRequest.of(0, 10));
        assertThat(result.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", comentario.getId())
                .hasFieldOrPropertyWithValue("idPersonaDocumento", personaDocumento.getId())
                .hasFieldOrPropertyWithValue("comentario", comentario.getComentario());
    }

    @Test
    void createComentario_success() {
        ComentariosAsistentesRecord record = ComentariosAsistentesSetUp.comentariosAsistentesRecord();

        ComentariosAsistentes comentario = ComentariosAsistentesSetUp.comentariosAsistentes();

        PersonaDocumento personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos();
        personaDocumento.setId(1);

        comentario.setPersonaDocumento(personaDocumento);
        comentario.setComentario(record.comentario());

        given(personaDocumentoRepository.findById(1)).willReturn(Optional.of(personaDocumento));
        given(comentariosAsistentesRepository.save(any(ComentariosAsistentes.class))).willReturn(comentario);

        ComentariosAsistentesResponse result = comentariosAsistentesService.create(record);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", comentario.getId())
                .hasFieldOrPropertyWithValue("idPersonaDocumento", personaDocumento.getId())
                .hasFieldOrPropertyWithValue("comentario", comentario.getComentario());
    }

    @Test
    void updateComentario_success() {
        ComentariosAsistentesRecord record = ComentariosAsistentesSetUp.comentariosAsistentesRecord();

        PersonaDocumento personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos();
        personaDocumento.setId(1);

        ComentariosAsistentes comentario = ComentariosAsistentesSetUp.comentariosAsistentes();
        comentario.setPersonaDocumento(personaDocumento);

        given(comentariosAsistentesRepository.findById(1)).willReturn(Optional.of(comentario));
        given(comentariosAsistentesRepository.save(any(ComentariosAsistentes.class))).willAnswer(invocation -> invocation.getArgument(0));

        ComentariosAsistentesResponse result = comentariosAsistentesService.update(1, record);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", comentario.getId())
                .hasFieldOrPropertyWithValue("idPersonaDocumento", personaDocumento.getId())
                .hasFieldOrPropertyWithValue("comentario", record.comentario());
    }

}