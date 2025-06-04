package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.conceptos.ConceptoSetUp.createConcepto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConceptoServiceTest {

    @Mock
    private ConceptoRepository mockConceptoRepository;

    @Mock
    private CarpetaRepository carpetaRepository;

    @Mock
    private TipoJuicioRepository tipoJuicioRepository;

    @InjectMocks
    private ConceptoService conceptoService;

    private Concepto concepto;

    @BeforeEach
    public void setUp() {
        concepto = createConcepto();
        ConceptoSetUp.createConceptoRecordResponse();
    }

    @Test
    void getAll_returns_page_when_data_exists() {
        List<Concepto> conceptosList = Collections.singletonList(concepto);
        Carpeta carpeta = CarpetaSetUp.create();

        given(carpetaRepository.findById(anyInt()))
                .willReturn(Optional.of(carpeta));

        given(mockConceptoRepository.findAllByTipoJuicio_IdOrNombreIn(1, List.of("Adjuntar", "Distribución", "RESGUARDO")))
                .willReturn(conceptosList);

        List<ConceptoRecordResponse> response = conceptoService.getAll(1);

        assertThat(response)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", concepto.getId())
                .hasFieldOrPropertyWithValue("nombre", "ADJUNTAR")
                .hasFieldOrPropertyWithValue("dias", concepto.getDias())
                .hasFieldOrPropertyWithValue("estado", concepto.getEstado());
    }

    @Test
    void getById_returns_conceptoRecordResponse() {
        given(mockConceptoRepository.findById(concepto.getId()))
                .willReturn(Optional.of(concepto));

        ConceptoRecordResponse result = conceptoService.findById(concepto.getId());
        assertThat(result).isOfAnyClassIn(ConceptoRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", concepto.getId())
                .hasFieldOrPropertyWithValue("nombre", concepto.getNombre())
                .hasFieldOrPropertyWithValue("dias", concepto.getDias())
                .hasFieldOrPropertyWithValue("estado", concepto.getEstado());
    }

    @Test
    void getById_returns_not_found() {
        Integer id = concepto.getId();
        given(mockConceptoRepository.findById(id))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> conceptoService.findById(concepto.getId())
        );

        assertThat(assertThrows.getMessage()).contains("Concepto no encontrado");
    }

    @Test
    void getAllConceptos_success() {
        ConceptoRecord conceptoRecord = new ConceptoRecord(1, "Adjuntar", 1, "", Estado.ACTIVE);
        Page<Concepto> conceptoPage = new PageImpl<>(List.of(concepto), PageRequest.of(0, 25), 1);
        given(mockConceptoRepository.findAllConceptos(anyString(), anyList(), any(PageRequest.class))).willReturn(conceptoPage);
        Page<ConceptoRecord> result = conceptoService.getAllConceptos(PageRequest.of(0, 25), "Adjuntar");

        assertThat(result.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", conceptoRecord.id())
                .hasFieldOrPropertyWithValue("concepto", conceptoRecord.concepto())
                .hasFieldOrPropertyWithValue("dias", conceptoRecord.dias())
                .hasFieldOrPropertyWithValue("nombreTipoJuicio", conceptoRecord.nombreTipoJuicio());
    }

    @Test
    void deleteConcepto() {
        Mockito.doThrow(DataIntegrityViolationException.class).when(mockConceptoRepository).deleteById(any());
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> conceptoService.delete(1)
        );
        assertThat(exception.getMessage()).contains(Messages.CONSTRAINT_ERROR);
    }

    @Test
    void findConceptoById() {
        given(mockConceptoRepository.findById(concepto.getId())).willReturn(Optional.of(concepto));
        ConceptoRecordJuicio result = conceptoService.findByConceptoById(concepto.getId());

        assertThat(result.conceptoId()).isEqualTo(concepto.getId());
        assertThat(result.concepto()).isEqualTo(concepto.getNombre());
        assertThat(result.dias()).isEqualTo(concepto.getDias());
        assertThat(result.estado()).isEqualTo(concepto.getEstado());
    }

    @Test
    void createConcepto_success() {
        TipoJuicio tipoJuicio = new TipoJuicio();
        concepto.setTipoJuicio(tipoJuicio.setId(1).setNombre("Tipo Juicio"));

        given(tipoJuicioRepository.findById(tipoJuicio.getId())).willReturn(Optional.of(tipoJuicio));
        given(mockConceptoRepository.save(any(Concepto.class))).willReturn(concepto);

        ConceptoRecord result = conceptoService.createConcepto(concepto);

        assertThat(result.id()).isEqualTo(concepto.getId());
        assertThat(result.concepto()).isEqualTo(concepto.getNombre());
        assertThat(result.dias()).isEqualTo(concepto.getDias());
        assertThat(result.nombreTipoJuicio()).isEqualTo(tipoJuicio.getNombre());
        assertThat(result.estado()).isEqualTo(concepto.getEstado());
    }

    @Test
    void updateConcepto_success() {
        Materia materia = new Materia();
        materia.setId(3).setNombre("Civil");

        TipoJuicio tipoJuicioExisting = new TipoJuicio();
        tipoJuicioExisting.setId(1).setNombre("Civil (Oral)").setMateria(materia);
        concepto.setTipoJuicio(tipoJuicioExisting);

        TipoJuicio tipoJuicioUpdated = new TipoJuicio();
        tipoJuicioUpdated.setId(2).setNombre("Civil (Tradicional)").setMateria(materia);

        Concepto updatedConcepto = createConcepto();
        updatedConcepto.setTipoJuicio(tipoJuicioUpdated);
        updatedConcepto.setNombre("Concepto actualizado").setDias(10).setEstado(Estado.INACTIVE);

        given(mockConceptoRepository.findById(concepto.getId())).willReturn(Optional.of(concepto));
        given(tipoJuicioRepository.findById(tipoJuicioUpdated.getId())).willReturn(Optional.of(tipoJuicioUpdated));
        given(mockConceptoRepository.save(any(Concepto.class))).willReturn(concepto);

        ConceptoRecord result = conceptoService.updateConcepto(updatedConcepto);

        assertThat(result.id()).isEqualTo(updatedConcepto.getId());
        assertThat(result.concepto()).isEqualTo(updatedConcepto.getNombre());
        assertThat(result.dias()).isEqualTo(updatedConcepto.getDias());
        assertThat(result.nombreTipoJuicio()).isEqualTo(tipoJuicioUpdated.getNombre());
        assertThat(result.estado()).isEqualTo(updatedConcepto.getEstado());
    }

    @Test
    void update_status_success() {
        given(mockConceptoRepository.findById(any())).willReturn(Optional.of(concepto));

        ConceptoRecord response = conceptoService.updateStatus(concepto.getId(), 1);
        assertThat(response).isOfAnyClassIn(ConceptoRecord.class)
                .hasFieldOrPropertyWithValue("id", response.id())
                .hasFieldOrPropertyWithValue("nombreTipoJuicio", response.nombreTipoJuicio())
                .hasFieldOrPropertyWithValue("estado", response.estado());

    }
}