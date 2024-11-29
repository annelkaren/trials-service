package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TipoAudienciaServiceTest {

    @Mock
    private TipoAudienciaRepository mockTipoAudienciaRepository;

    @Mock
    private DocumentoRepository mockDocumentoRepository;

    @InjectMocks
    private TipoAudienciaService tipoAudenciaService;

    private TipoAudiencia tipoAudiencia;
    private TipoAudienciaRecord tipoAudienciaRecord;
    private Documento documento;
    private Materia materia;
    private TipoJuicio tipoJuicio;
    private Carpeta carpeta;
    private TipoSistema tipoSistema;

    @BeforeEach
    public void setUp() {
        tipoAudiencia = TipoAudienciaSetUp.createTipoAudencia();
        tipoAudienciaRecord = TipoAudienciaSetUp.createTipoAudienciaRecord();
        materia = MateriaSetUp.createMateria();
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        carpeta = CarpetaSetUp.create();
        documento = DocumentoSetUp.create(tipoJuicio);
        tipoSistema = TipoSistemaSetUp.createTipoSistema();
    }

    @Test
    void getAll_returns_page_when_data_exists() {
        List<TipoAudiencia> listPage = Collections.singletonList(tipoAudiencia);
        given(mockTipoAudienciaRepository.findAll(any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<TipoAudienciaRecord> page = tipoAudenciaService.getAll(PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", tipoAudiencia.getId())
                .hasFieldOrPropertyWithValue("nombre", tipoAudiencia.getNombre());
    }

    @Test
    void getById_returns_conceptoRecordResponse() {
        given(mockTipoAudienciaRepository.findById(tipoAudiencia.getId()))
                .willReturn(Optional.of(tipoAudiencia));

        TipoAudienciaRecord result = tipoAudenciaService.findById(tipoAudiencia.getId());
        assertThat(result).isOfAnyClassIn(TipoAudienciaRecord.class)
                .hasFieldOrPropertyWithValue("id", tipoAudiencia.getId())
                .hasFieldOrPropertyWithValue("nombre", tipoAudiencia.getNombre());
    }

    @Test
    void getById_returns_not_found() {
        given(mockTipoAudienciaRepository.findById(tipoAudiencia.getId()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> tipoAudenciaService.findById(tipoAudiencia.getId())
        );

        assertThat(assertThrows.getMessage()).contains("Tipo audiencia no encontrada");
    }

    @Test
    void findRubrosByDocumentoId() {
        documento.setId(51);
        tipoJuicio.setTipoSistema(tipoSistema);
        carpeta.setTipoJuicio(tipoJuicio);
        documento.setCarpeta(carpeta);
        documento.getCarpeta().getTipoJuicio().setMateria(materia);
        tipoAudiencia.setId(100);
        tipoAudiencia.setNombre("Desahogo de Pruebas");
        tipoAudiencia.setMateria(materia);
        tipoAudiencia.setTipoSistema(tipoSistema);

        List<TipoAudiencia> tipoAudienciaList = Collections.singletonList(tipoAudiencia);

        given(mockDocumentoRepository.findById(51)).willReturn(Optional.of(documento));

        given(mockTipoAudienciaRepository.findByMateriaAndTipoSistemaAndNombreContainingIgnoreCase(materia, tipoSistema, "any", PageRequest.of(0, 10)))
                .willReturn(new PageImpl<>(tipoAudienciaList, PageRequest.of(0, 10), tipoAudienciaList.size()));

        Page<TipoAudienciaRecord> result = tipoAudenciaService.findTipoAudienciaByDocumentoId(51, PageRequest.of(0, 10), "any");
        assertThat(result.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", tipoAudiencia.getId())
                .hasFieldOrPropertyWithValue("nombre", tipoAudiencia.getNombre());
    }

    @Test
    void obtenerTipoAudiencia() {
        String nombreAudiencia = "Desahogo de Pruebas";
        tipoAudiencia.setNombre(nombreAudiencia);

        given(mockTipoAudienciaRepository.findByNombre(nombreAudiencia))
                .willReturn(tipoAudiencia);

        TipoAudiencia result = tipoAudenciaService.obtenerTipoAudiencia(nombreAudiencia);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", tipoAudiencia.getId())
                .hasFieldOrPropertyWithValue("nombre", nombreAudiencia);
    }

    @Test
    void getAll_returns_list_of_tipoAudienciaRecords() {
        List<TipoAudiencia> tipoAudienciaList = Collections.singletonList(tipoAudiencia);
        PageRequest pageRequest = PageRequest.of(0, 10);

        given(mockTipoAudienciaRepository.findAll(pageRequest))
                .willReturn(new PageImpl<>(tipoAudienciaList, pageRequest, tipoAudienciaList.size()));

        List<TipoAudienciaRecord> result = tipoAudenciaService.getAll(pageRequest, "Desahogo");
        assertThat(result)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", tipoAudiencia.getId())
                .hasFieldOrPropertyWithValue("nombre", tipoAudiencia.getNombre());
    }

}