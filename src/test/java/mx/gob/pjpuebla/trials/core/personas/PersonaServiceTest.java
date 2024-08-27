package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.materias.MateriaSetUp.createMateria;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    MateriaRepository mockPersonaRepository;

    @InjectMocks
    MateriaService target;

    private Materia validPersona;

    @BeforeEach
    public void setUp() {
        validPersona = createMateria();
    }

    @Test
    void getById_return_materia() {
        given(mockPersonaRepository.findByIdAndEstado(validPersona.getId(), validPersona.getEstado()))
                .willReturn(Optional.ofNullable(validPersona));

        MateriaRecord mr = target.findById(validPersona.getId());
        assertThat(mr).isOfAnyClassIn(MateriaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre());
    }

    @Test
    void getById_return_not_found() {
        given(mockPersonaRepository.findByIdAndEstado(validPersona.getId(), validPersona.getEstado()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.findById(validPersona.getId());
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");

    }
}