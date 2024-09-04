package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.Estado;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersona;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    PersonaRepository mockPersonaRepository;

    @InjectMocks
    PersonaService personaService;

    @Mock
    DomicilioService domicilioService;

    private Persona validPersona;
    private Domicilio validDomicilio;

    @BeforeEach
    public void setUp() {
        validPersona = createPersona();
        validDomicilio = new Domicilio();
    }

    @Test
    void getAll_return_page() {
        List<Persona> listPage = Collections.singletonList(validPersona);
        given(mockPersonaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<PersonaRecordResponse> page = personaService.getAll(validPersona, PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre() + " "
                        + validPersona.getApellidoPaterno() + " " + validPersona.getApellidoMaterno());
    }

    @Test
    void getById_return_persona() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockPersonaRepository.findByIdAndEstadoIn(validPersona.getId(), estados))
                .willReturn(Optional.ofNullable(validPersona));

        PersonaRecord mr = personaService.findById(validPersona.getId());
        assertThat(mr).isOfAnyClassIn(PersonaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Long personaId = validPersona.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockPersonaRepository.findByIdAndEstadoIn(personaId, estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    personaService.findById(personaId);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");

    }

    @Test
    void create() {
        validPersona.setDomicilio(validDomicilio);
        given(domicilioService.save(validDomicilio))
                .willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona))
                .willReturn(validPersona);

        PersonaRecord response = personaService.create(validPersona);

        assertThat(response).isOfAnyClassIn(PersonaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre());
    }

    @Test
    void update() {
        validPersona.setDomicilio(validDomicilio);
        given(domicilioService.save(validDomicilio))
                .willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona))
                .willReturn(validPersona);

        PersonaRecord response = personaService.update(validPersona);

        assertThat(response).isOfAnyClassIn(PersonaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre());
    }

    @Test
    void update_return_optimistic_exception() {
        validPersona.setDomicilio(validDomicilio);
        given(domicilioService.save(validDomicilio))
                .willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> {
                    personaService.update(validPersona);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona modificada por otro usuario");
    }
}