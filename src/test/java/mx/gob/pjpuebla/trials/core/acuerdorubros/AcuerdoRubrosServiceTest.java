package mx.gob.pjpuebla.trials.core.acuerdorubros;

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
class AcuerdoRubrosServiceTest {

    @Mock
    private AcuerdoRubrosRepository mockAcuerdoRubrosRepository;

    @Mock
    private DocumentoRepository mockDocumentoRepository;

    @InjectMocks
    private AcuerdoRubrosService acuerdoRubrosService;

    private AcuerdoRubros acuerdoRubros;
    private AcuerdoRubrosRecord acuerdoRubrosRecord;
    private Documento documento;
    private Materia materia;
    private TipoJuicio tipoJuicio;
    private Carpeta carpeta;
    private TipoSistema tipoSistema;

    @BeforeEach
    public void setUp() {
        acuerdoRubros = AcuerdoRubrosSetUp.createAcuerdoRubro();
        acuerdoRubrosRecord = AcuerdoRubrosSetUp.createAcuerdoRubrosRecord();
        materia = MateriaSetUp.createMateria();
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        carpeta = CarpetaSetUp.create();
        documento = DocumentoSetUp.create(tipoJuicio);
        tipoSistema = TipoSistemaSetUp.createTipoSistema();
    }

    @Test
    void getAll_returns_page_when_data_exists() {
        List<AcuerdoRubros> listPage = Collections.singletonList(acuerdoRubros);
        given(mockAcuerdoRubrosRepository.findAll(any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<AcuerdoRubrosRecord> page = acuerdoRubrosService.getAll(PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", acuerdoRubros.getId())
                .hasFieldOrPropertyWithValue("nombre", acuerdoRubros.getNombre());
    }

    @Test
    void getById_returns_conceptoRecordResponse() {
        given(mockAcuerdoRubrosRepository.findById(acuerdoRubros.getId()))
                .willReturn(Optional.of(acuerdoRubros));

        AcuerdoRubrosRecord result = acuerdoRubrosService.findById(acuerdoRubros.getId());
        assertThat(result).isOfAnyClassIn(AcuerdoRubrosRecord.class)
                .hasFieldOrPropertyWithValue("id", acuerdoRubros.getId())
                .hasFieldOrPropertyWithValue("nombre", acuerdoRubros.getNombre());
    }

    @Test
    void getById_returns_not_found() {
        given(mockAcuerdoRubrosRepository.findById(acuerdoRubros.getId()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> acuerdoRubrosService.findById(acuerdoRubros.getId())
        );

        assertThat(assertThrows.getMessage()).contains("Acuerdo rubro no encontrado");
    }

    @Test
    void findRubrosByDocumentoId_returns_page_when_document_exists() {
        documento.setId(51);
        tipoJuicio.setTipoSistema(tipoSistema);
        carpeta.setTipoJuicio(tipoJuicio);
        documento.setCarpeta(carpeta);
        materia.setNombre("FAMILIAR");
        documento.getCarpeta().getTipoJuicio().setMateria(materia);
        acuerdoRubros.setId(100);
        acuerdoRubros.setNombre("ACEPTACION DE CARGO");
        acuerdoRubros.setMateria(materia);
        acuerdoRubros.setTipoSistema(tipoSistema);

        List<AcuerdoRubros> acuerdoRubrosList = Collections.singletonList(acuerdoRubros);

        given(mockDocumentoRepository.findById(51)).willReturn(Optional.of(documento));

        given(mockAcuerdoRubrosRepository.findByMateriaAndTipoSistemaAndNombreContainingIgnoreCase(materia, tipoSistema, "any", PageRequest.of(0, 10)))
                .willReturn(new PageImpl<>(acuerdoRubrosList, PageRequest.of(0, 10), acuerdoRubrosList.size()));

        Page<AcuerdoRubrosRecord> result = acuerdoRubrosService.findRubrosByDocumentoId(51, PageRequest.of(0, 10), "any");
        assertThat(result.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", acuerdoRubros.getId())
                .hasFieldOrPropertyWithValue("nombre", acuerdoRubros.getNombre());
    }

}