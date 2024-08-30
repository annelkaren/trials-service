package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersona;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    PersonaRepository mockPersonaRepository;
    @Mock
    DomicilioRepository mockDomicilioRepository;

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
    void getById_return_persona() {
        given(mockPersonaRepository.findById(validPersona.getId()))
                .willReturn(Optional.ofNullable(validPersona));

        PersonaRecord mr = personaService.findById(validPersona.getId());
        assertThat(mr).isOfAnyClassIn(PersonaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre());
    }

    @Test
    void getById_return_not_found() {
        given(mockPersonaRepository.findById(validPersona.getId()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    personaService.findById(validPersona.getId());
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
}