package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JuzgadoServiceTest {

    @InjectMocks
    JuzgadoService juzgadoService;

    @Mock
    JuzgadoRepository juzgadoRepository;

    @Mock
    SedeRepository sedeRepository;

    @Mock
    MateriaRepository materiaRepository;

    @Mock
    private Pageable pageableMock;

    private Juzgado juzgado;
    private JuzgadoRecord juzgadoRecord;

    @BeforeEach
    public void setUp() {
        Materia materia = MateriaSetUp.createMateria();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgadoRecord = JuzgadoSetUp.createJuzgadoRecord(juzgado, materia.getId(), sede.getId());
    }

    @Test
    void getAll_return_page() {
        List<Juzgado> listPage = Collections.singletonList(juzgado);
        given(juzgadoRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<JuzgadoRecordResponse> page = juzgadoService.getAll(juzgado, PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void getById_return_juzgadoRecord() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(juzgadoRepository.findByIdAndEstadoIn(juzgado.getId(), estados))
                .willReturn(Optional.ofNullable(juzgadoRecord));

        JuzgadoRecord result = juzgadoService.findById(juzgado.getId());
        assertThat(result).isOfAnyClassIn(JuzgadoRecord.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("materiaId", juzgado.getMateria().getId())
                .hasFieldOrPropertyWithValue("sedeId", juzgado.getSede().getId());
    }

    @Test
    void getById_return_not_found() {
        Integer id = juzgado.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(juzgadoRepository.findByIdAndEstadoIn(juzgado.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.findById(id);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Juzgado no encontrado");
    }

    @Test
    void create() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);

        JuzgadoRecordResponse response = juzgadoService.create(juzgado);

        assertThat(response).isOfAnyClassIn(JuzgadoRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void update() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);

        JuzgadoRecordResponse response = juzgadoService.update(juzgado);

        assertThat(response).isOfAnyClassIn(JuzgadoRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void update_return_optimistic_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> {
                    juzgadoService.update(juzgado);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Juzgado modificado por otro usuario");
    }
}
