package mx.gob.pjpuebla.trials.core.salas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.util.Estado;

@ExtendWith(MockitoExtension.class)
public class SalaServiceTest {

    @Mock
    SalaRepository mockSalaRepository;
    @Mock
    PersonaRepository personaRepository;
    @Mock
    BloqueRepository bloqueRepository;
    @Mock
    MateriaRepository materiaRepository;
    @Mock
    DistritoRepository distritoRepository;
    @Mock
    DomicilioRepository domicilioRepository;
    @Mock
    SedeRepository sedeRepository;
    @Mock
    JuzgadoRepository juzgadoRepository;

    @InjectMocks
    SalaService salaService;

    private Sala sala;
    private SalaRecord salaRecord;
    private  SalaRecordResponse salaRecordResponse;
    private Persona juez;
    private Bloque bloque;
    private Materia materia;
    private Distrito distrito;
    private Domicilio domicilio;
    private Juzgado juzgado;

    @BeforeEach
    public void setUp() {
        juez = PersonaSetUp.createPersona();
        bloque = BloqueSetUp.createBloque();
        materia = MateriaSetUp.createMateria();
        distrito = DistritoSetUp.createDistrito();
        domicilio = DomicilioSetUp.createDomicilio();

        Sede sede = SedeSetUp.createSede(Estado.ACTIVE);
        sede.setDomicilio(domicilio);
        sede.setDistrito(distrito);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);

        Sala salaLocal = SalaSetUp.createSala(Estado.ACTIVE);
        salaLocal.setBloque(bloque);
        salaLocal.setJuzgado(juzgado);
        salaLocal.setJuez(juez);

        sala = salaLocal; 
        salaRecord = SalaSetUp.salaRecord();
        salaRecordResponse = SalaSetUp.salaRecordResponse();
        
    }

    @Test
    void getAll_return_page() {
        List<Sala> listPage = Collections.singletonList(sala);
        given(mockSalaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<SalaRecord> page = salaService.getAll(sala, PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", sala.getId())
                .hasFieldOrPropertyWithValue("nombre", sala.getNombre());
    }


    @Test
    void getById_return_salaRecord() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockSalaRepository.findByIdAndEstadoIn(sala.getId(), estados))
            .willReturn(Optional.of(salaRecordResponse));

        SalaRecordResponse result = salaService.findById(sala.getId());
        assertThat(result).isOfAnyClassIn(SalaRecordResponse.class)
            .hasFieldOrPropertyWithValue("id", sala.getId())
            .hasFieldOrPropertyWithValue("nombre", sala.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer id = sala.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockSalaRepository.findByIdAndEstadoIn(sala.getId(), estados))
            .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    salaService.findById(id);
                });

        assertThat(assertThrows.getMessage()).contains("Sala no encontrada");
    };

    @Test
    void create() {
        given(mockSalaRepository.save(sala))
            .willReturn(sala);
        
        Integer response =  salaService.create(sala);

        assertThat(response).isEqualTo(sala.getId());
    }

    @Test
    void update() {
        given(mockSalaRepository.save(sala))
            .willReturn(sala);

        Integer response = salaService.update(sala);

        assertThat(response).isEqualTo(sala.getId());
    }

    @Test
    void update_return_optimistic_exception() {
        given(mockSalaRepository.save(sala)).willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> {
                    salaService.update(sala);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Sala modificada por otro usuario");
    }



}
