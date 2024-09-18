package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio.RelJuzgadoTipoJuicio;
import mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio.RelJuzgadoTipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
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
    TipoJuicioRepository tipoJuicioRepository;
    @Mock
    MateriaRepository materiaRepository;
    @Mock
    TipoSistemaRepository tipoSistemaRepository;
    @Mock
    RelJuzgadoTipoJuicioRepository relJuzgadoTipoJuicioRepository;

    private Juzgado juzgado;
    private JuzgadoRecord juzgadoRecord;
    private RelJuzgadoTipoJuicio relJuzgadoTipoJuicio;

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

        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistemaRepository.save(tipoSistema);
        TipoJuicio tipoJuicio = createTipoJuicio(tipoSistema, materia);
        tipoJuicioRepository.save(tipoJuicio);

        relJuzgadoTipoJuicio = new RelJuzgadoTipoJuicio();
        relJuzgadoTipoJuicio.setJuzgado(juzgado);
        relJuzgadoTipoJuicio.setTipoJuicio(tipoJuicio);
        relJuzgadoTipoJuicioRepository.save(relJuzgadoTipoJuicio);
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
    void getById_return_juzgadoDTO() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(juzgadoRepository.findByIdAndEstadoIn(juzgado.getId(), estados))
                .willReturn(Optional.ofNullable(juzgadoRecord));

        JuzgadoDTO result = juzgadoService.findById(juzgado.getId());
        assertThat(result).isOfAnyClassIn(JuzgadoDTO.class);
        assertThat(result.getJuzgado()).isOfAnyClassIn(Juzgado.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer id = juzgado.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(juzgadoRepository.findByIdAndEstadoIn(juzgado.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.findById(id)
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
        given(tipoJuicioRepository.findById(relJuzgadoTipoJuicio.getTipoJuicio().getId()))
                .willReturn(Optional.ofNullable(relJuzgadoTipoJuicio.getTipoJuicio()));
        given(relJuzgadoTipoJuicioRepository.save(relJuzgadoTipoJuicio))
                .willReturn(relJuzgadoTipoJuicio);

        List<TipoJuicioRecord> tipoJuicioRecords = new ArrayList<>();
        tipoJuicioRecords.add(new TipoJuicioRecord(
                relJuzgadoTipoJuicio.getTipoJuicio().getId(),
                relJuzgadoTipoJuicio.getTipoJuicio().getNombre(),
                new TipoSistemaRecord(null, null),
                new MateriaRecord(null, null))
        );

        JuzgadoRecordResponse response = juzgadoService.create(juzgado, tipoJuicioRecords);

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
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.ofNullable(juzgado));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);
        given(tipoJuicioRepository.findById(relJuzgadoTipoJuicio.getTipoJuicio().getId()))
                .willReturn(Optional.ofNullable(relJuzgadoTipoJuicio.getTipoJuicio()));
        given(relJuzgadoTipoJuicioRepository.save(relJuzgadoTipoJuicio))
                .willReturn(relJuzgadoTipoJuicio);

        List<TipoJuicioRecord> tipoJuicioRecords = new ArrayList<>();
        tipoJuicioRecords.add(new TipoJuicioRecord(
                relJuzgadoTipoJuicio.getTipoJuicio().getId(),
                relJuzgadoTipoJuicio.getTipoJuicio().getNombre(),
                new TipoSistemaRecord(null, null),
                new MateriaRecord(null, null))
        );

        JuzgadoRecordResponse response = juzgadoService.update(juzgado, tipoJuicioRecords);

        assertThat(response).isOfAnyClassIn(JuzgadoRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void create_return_materias_notFound_exception() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.create(juzgado, null);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Materia no encontrada");
    }


    @Test
    void create_return_sede_notFound_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.create(juzgado, null);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
    }

    @Test
    void create_return_tipoJuicio_notFound_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);

        List<TipoJuicioRecord> tipoJuicioRecords = new ArrayList<>();
        tipoJuicioRecords.add(new TipoJuicioRecord(
                relJuzgadoTipoJuicio.getTipoJuicio().getId(),
                relJuzgadoTipoJuicio.getTipoJuicio().getNombre(),
                new TipoSistemaRecord(null, null),
                new MateriaRecord(null, null))
        );

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.create(juzgado, tipoJuicioRecords);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Tipo Juicio no encontrado");
    }

    @Test
    void update_return_optimistic_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.ofNullable(juzgado));
        given(juzgadoRepository.save(juzgado))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> juzgadoService.update(juzgado, null)
        );

        assertThat(assertThrows.getMessage()).contains("Juzgado modificado por otro usuario");
    }

    @Test
    void update_return_materias_notFound_exception() {
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.ofNullable(juzgado));
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.update(juzgado, null);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Materia no encontrada");
    }

    @Test
    void update_return_juzgado_notFound_exception() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.update(juzgado, null);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Juzgado no encontrado");
    }

    @Test
    void update_return_sede_notFound_exception() {
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.ofNullable(juzgado));
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.update(juzgado, null);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
    }

    @Test
    void update_return_tipoJuicio_notFound_exception() {
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.ofNullable(juzgado));
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);

        List<TipoJuicioRecord> tipoJuicioRecords = new ArrayList<>();
        tipoJuicioRecords.add(new TipoJuicioRecord(
                relJuzgadoTipoJuicio.getTipoJuicio().getId(),
                relJuzgadoTipoJuicio.getTipoJuicio().getNombre(),
                new TipoSistemaRecord(null, null),
                new MateriaRecord(null, null))
        );

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    juzgadoService.update(juzgado, tipoJuicioRecords);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Tipo Juicio no encontrado");
    }

}
