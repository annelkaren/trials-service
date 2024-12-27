package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionRepository;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionSetUp;
import mx.gob.pjpuebla.trials.util.enums.Asistencia;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.records.RegistrarAsistenciaAudienciaRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AsistenciaAudienciaServiceTest {

    @Mock
    private AsistenciaAudienciaRepository asistenciaAudienciaRepository;

    @Mock
    private PersonaDocumentoRepository personaDocumentoRepository;

    @Mock
    private AudienciaRepository audienciaRepository;

    @Mock
    private DocumentoIdentificacionRepository documentoIdentificacionRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private DigitalizacionService digitalizacionService;

    @InjectMocks
    private AsistenciaAudienciaService asistenciaAudienciaService;

    private AsistenciaAudiencia asistenciaAudiencia;
    private PersonaDocumento personaDocumento;
    private Audiencia audiencia;
    private DocumentoIdentificacion documentoIdentificacion;
    private Documento documento;
    private MultipartFile multipartFile;

    @BeforeEach
    public void setUp() {
        asistenciaAudiencia = AsistenciaAudienciaSetUp.asistenciaAudiencia();

        personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos();
        personaDocumento.setId(1);

        audiencia = new Audiencia();
        audiencia.setId(1);
        audiencia.setCarpeta(new Carpeta());

        documentoIdentificacion = DocumentoIdentificacionSetUp.createDocIdentificacion();
        documentoIdentificacion.setId(1);

        documento = new Documento();
        documento.setId(1);
        documento.setRuta("ruta/documento.pdf");

        multipartFile = Mockito.mock(MultipartFile.class);
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

    @Test
    void registrarAsistencia_success() {
        RegistrarAsistenciaAudienciaRecord record = new RegistrarAsistenciaAudienciaRecord(1, 1, 1);

        given(personaDocumentoRepository.findById(1)).willReturn(Optional.of(personaDocumento));
        given(audienciaRepository.findById(1)).willReturn(Optional.of(audiencia));
        given(documentoIdentificacionRepository.findById(1)).willReturn(Optional.of(documentoIdentificacion));
        given(documentoRepository.save(any(Documento.class))).willReturn(documento);
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));
        given(asistenciaAudienciaRepository.save(any(AsistenciaAudiencia.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        AsistenciaAudiencia result = asistenciaAudienciaService.registrarAsistencia(record, multipartFile);

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("personaDocumento", personaDocumento)
                .hasFieldOrPropertyWithValue("audiencia", audiencia)
                .hasFieldOrPropertyWithValue("documentoIdentificacion", documentoIdentificacion)
                .hasFieldOrPropertyWithValue("urlDocumento", documento.getRuta())
                .hasFieldOrPropertyWithValue("asistencia", Asistencia.SI);
    }

}