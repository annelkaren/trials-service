package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AsistenciaAudienciaServiceTest {

    @Mock
    private AsistenciaAudienciaRepository asistenciaAudienciaRepository;

    @InjectMocks
    private AsistenciaAudienciaService asistenciaAudienciaService;

    private AsistenciaAudiencia asistenciaAudiencia;

    @BeforeEach
    public void setUp() {
        asistenciaAudiencia = AsistenciaAudienciaSetUp.asistenciaAudiencia();
    }

    @Test
    void getAll_return_page() {
        List<AsistenciaAudiencia> listPage = Collections.singletonList(asistenciaAudiencia);
        given(asistenciaAudienciaRepository.findAll(any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<AsistenciaAudienciaResponse> page = asistenciaAudienciaService.getAll(PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", asistenciaAudiencia.getId())
                .hasFieldOrPropertyWithValue("personaDocumentoId", asistenciaAudiencia.getPersonaDocumento().getId())
                .hasFieldOrPropertyWithValue("asistencia", asistenciaAudiencia.getAsistencia())
                .hasFieldOrPropertyWithValue("documentoIdentificacion", asistenciaAudiencia.getDocumentoIdentificacion().getName())
                .hasFieldOrPropertyWithValue("urlDocumento", asistenciaAudiencia.getUrlDocumento());
    }

}