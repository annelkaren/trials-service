package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

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

import static mx.gob.pjpuebla.trials.core.especialidadJuzgado.EspecialidadesSetUp.createEspecialidades;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EspecialidadesServiceTest {

    @Mock
    EspecialidadesRepository mockEspecialidadesRepository;

    @InjectMocks
    EspecialidadesService target;

    private Especialidades validEspecialidades;

    @BeforeEach
    public void setUp() {
        validEspecialidades = createEspecialidades();
    }

    @Test
    void getAll_return_page() {
        List<Especialidades> listPage = Collections.singletonList(validEspecialidades);
        given(mockEspecialidadesRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<EspecialidadesRecord> page = target.getAllActive(PageRequest.of(1, listPage.size()), validEspecialidades);

        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", validEspecialidades.getId())
                .hasFieldOrPropertyWithValue("nombre", validEspecialidades.getNombre());
    }

    @Test
    void getById_return_especialidad() {
        given(mockEspecialidadesRepository.findByIdAndEstado(validEspecialidades.getId(), validEspecialidades.getEstado()))
                .willReturn(Optional.ofNullable(validEspecialidades));

        EspecialidadesRecord er = target.findById(validEspecialidades.getId());
        assertThat(er).isOfAnyClassIn(EspecialidadesRecord.class)
                .hasFieldOrPropertyWithValue("id", validEspecialidades.getId())
                .hasFieldOrPropertyWithValue("nombre", validEspecialidades.getNombre());
    }

    @Test
    void getById_return_not_found() {
        given(mockEspecialidadesRepository.findByIdAndEstado(validEspecialidades.getId(), validEspecialidades.getEstado()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
          NotFoundException.class,
                () -> {
                    target.findById(validEspecialidades.getId());
                }
        );

        assertThat(assertThrows.getMessage())
                .contains("Especialidad de Juzgado no encontrada");
    }
}