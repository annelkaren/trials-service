package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Views;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OficialiaServiceTest {

    @InjectMocks
    OficialiaService oficialiaService;

    @Mock
    OficialiaRepository oficialiaRepository;

    @Mock
    TipoOficialiaRepository tipoOficialiaRepository;

    @Mock
    SedeRepository sedeRepository;

    @Mock
    private JuzgadoRepository juzgadoRepository;
    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private Pageable pageableMock;


    private Oficialia oficialia;
    private OficialiaRecord oficialiaRecord;
    private TipoOficialia tipoOficialia;
    private  Oficialia oficialia1;
    private  Juzgado juzgado;
    private Materia materia;

    @BeforeEach
    public void setUp() {
        tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        juzgado = JuzgadoSetUp.createJuzgado();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        materia = MateriaSetUp.createMateria();

        oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede)
                .setMateria(List.of(materia));
        oficialia.setJuzgado(juzgado);
        oficialiaRecord = OficialiaSetUp.createOficialiaRecord(oficialia, new TipoOficialiaRecord(tipoOficialia.getId(), tipoOficialia.getNombre()), new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado()));
    }


    @Test
    void getAll_return_page() {
        List<Oficialia> listPage = Collections.singletonList(oficialia);
        given(oficialiaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<OficialiaRecord> page = oficialiaService.getAllActive(PageRequest.of(1, listPage.size()), oficialia);
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("version", oficialia.getVersion())
                .hasFieldOrPropertyWithValue("estado", oficialia.getEstado())
                .hasFieldOrPropertyWithValue("responsable", oficialia.getResponsable())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());

        assertThat(oficialia.getTipoOficialia())
                .hasFieldOrPropertyWithValue("id", oficialia.getTipoOficialia().getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getTipoOficialia().getNombre());

        assertThat(oficialia.getSede())
                .hasFieldOrPropertyWithValue("id", oficialia.getSede().getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getSede().getNombre())
                .hasFieldOrPropertyWithValue("estado", oficialia.getSede().getEstado());
    }

    @Test
    void getById_return_oficialia() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(oficialiaRepository.findByIdAndEstadoIn(oficialia.getId(), estados))
                .willReturn(Optional.ofNullable(oficialiaRecord));

        OficialiaRecord mr = oficialiaService.findById(oficialia.getId());
        assertThat(mr).isOfAnyClassIn(OficialiaRecord.class)
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer personaId = oficialia.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(oficialiaRepository.findByIdAndEstadoIn(personaId, estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> oficialiaService.findById(personaId)
        );

        assertThat(assertThrows.getMessage()).contains("Oficialia no encontrada");
    }

    @Test
    void create() {
        given(sedeRepository.findById(oficialia.getSede().getId()))
                .willReturn(Optional.ofNullable(oficialia.getSede()));
        given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                .willReturn(Optional.of(tipoOficialia));
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.of(juzgado));

        given(oficialiaRepository.save(oficialia))
                .willReturn(oficialia);

        OficialiaRecordResponse response = oficialiaService.create(oficialia);

        assertThat(response).isOfAnyClassIn(OficialiaRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }



    @Test
    void update() {
        given(oficialiaRepository.findById(oficialia.getId()))
                .willReturn(Optional.ofNullable(oficialia));
        given(sedeRepository.findById(oficialia.getSede().getId()))
                .willReturn(Optional.ofNullable(oficialia.getSede()));
        given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                .willReturn(Optional.of(tipoOficialia));
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.of(oficialia.getJuzgado()));

        given(oficialiaRepository.save(oficialia))
                .willReturn(oficialia);


        OficialiaRecordResponse response = oficialiaService.update(oficialia);

        assertThat(response).isOfAnyClassIn(OficialiaRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }

    @Test
    void update_return_optimistic_exception() {
        given(oficialiaRepository.findById(oficialia.getId()))
                .willReturn(Optional.ofNullable(oficialia));
        given(sedeRepository.findById(oficialia.getSede().getId()))
                .willReturn(Optional.ofNullable(oficialia.getSede()));
        given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                .willReturn(Optional.of(tipoOficialia));
        given(oficialiaRepository.save(oficialia))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.of(oficialia.getJuzgado()));


        InvalidVersionException assertThrows = assertThrows(
                InvalidVersionException.class,
                () -> oficialiaService.update(oficialia)
        );

        assertThat(assertThrows.getMessage()).contains("Version modificada por otro usuario");
    }

    public static List<Materia> listcreateMateria() {
        Materia materia1 = new Materia()
                .setNombre("PENAL")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        materia1.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        Materia materia2 = new Materia()
                .setNombre("CIVIL")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        materia2.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        return Arrays.asList(materia1, materia2);
    }

    public static Oficialia createOficialia(TipoOficialia tipoOficialia, Sede sede, List<Materia> materias, Juzgado juzgado) {
        Oficialia oficialia = new Oficialia()
                .setId(1)
                .setVersion(0)
                .setEstado(Estado.ACTIVE)
                .setJuzgado(juzgado)
                .setTipoOficialia(tipoOficialia)
                .setNombre("Común")
                .setResponsable("Responsable")
                .setSede(sede)
                .setMateria(materias);
        oficialia.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return oficialia;
    }
}

