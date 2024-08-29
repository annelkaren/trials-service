package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersona;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    PersonaRepository mockPersonaRepository;

    @InjectMocks
    PersonaService target;

    private Persona validPersona;

    @BeforeEach
    public void setUp() {
        validPersona = createPersona();
    }

    @Test
    void getById_return_persona() {
        given(mockPersonaRepository.findById(validPersona.getId()))
                .willReturn(Optional.ofNullable(validPersona));

        PersonaRecord mr = target.findById(validPersona.getId());
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
                    target.findById(validPersona.getId());
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");

    }

    @Test
    void create() {
        given(mockPersonaRepository.save(validPersona)).willReturn(validPersona);

        Persona mr = target.create(validPersona);

        assertThat(mr).isOfAnyClassIn(Persona.class).isNotNull();
    }

    @Test
    void update() {
        given(mockPersonaRepository.save(validPersona)).willThrow(OptimisticLockingFailureException.class);

        Persona mr = target.update(validPersona);

        assertThat(mr).isOfAnyClassIn(Persona.class).isNotNull();
    }
}