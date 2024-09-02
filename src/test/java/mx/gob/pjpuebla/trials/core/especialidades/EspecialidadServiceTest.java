package mx.gob.pjpuebla.trials.core.especialidades;

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

import static mx.gob.pjpuebla.trials.core.especialidades.EspecialidadSetUp.createEspecialidad;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EspecialidadServiceTest {

    @Mock
    EspecialidadRepository mockEspecialidadRepository;

    @InjectMocks
    EspecialidadService target;

    private Especialidad validEspecialidad;

    @BeforeEach
    public void setUp() {
        validEspecialidad = createEspecialidad();
    }

    @Test
    void getAll_return_page() {
        List<Especialidad> listPage = Collections.singletonList(validEspecialidad);
        given(mockEspecialidadRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<EspecialidadRecord> page = target.getAllActive(PageRequest.of(1, listPage.size()), validEspecialidad);

        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", validEspecialidad.getId())
                .hasFieldOrPropertyWithValue("nombre", validEspecialidad.getNombre());
    }

    @Test
    void getById_return_especialidad() {
        given(mockEspecialidadRepository.findByIdAndEstado(validEspecialidad.getId(), validEspecialidad.getEstado()))
                .willReturn(Optional.ofNullable(validEspecialidad));

        EspecialidadRecord er = target.findById(validEspecialidad.getId());
        assertThat(er).isOfAnyClassIn(EspecialidadRecord.class)
                .hasFieldOrPropertyWithValue("id", validEspecialidad.getId())
                .hasFieldOrPropertyWithValue("nombre", validEspecialidad.getNombre());
    }

    @Test
    void getById_return_not_found() {
        given(mockEspecialidadRepository.findByIdAndEstado(validEspecialidad.getId(), validEspecialidad.getEstado()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
          NotFoundException.class,
                () -> {
                    target.findById(validEspecialidad.getId());
                }
        );

        assertThat(assertThrows.getMessage())
                .contains("Especialidad de Juzgado no encontrada");
    }
}