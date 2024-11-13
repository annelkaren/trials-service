package mx.gob.pjpuebla.trials.core.materias;

import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.apache.commons.lang3.StringUtils;
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
class MateriaServiceTest {

    @Mock
    MateriaRepository mockMateriaRepository;

    @InjectMocks
    MateriaService target;

    private Materia validMateria;

    @BeforeEach
    public void setUp() {
        validMateria = createMateria();
    }

    @Test
    void getAll_return_page() {
        List<Materia> listPage = Collections.singletonList(validMateria);
        given(mockMateriaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<MateriaRecord> page = target.getAllActive(PageRequest.of(1, listPage.size()), validMateria);
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validMateria.getId())
                .hasFieldOrPropertyWithValue("nombre", StringUtils.capitalize(validMateria.getNombre().toLowerCase()));
    }

    @Test
    void getById_return_materia() {
        given(mockMateriaRepository.findByIdAndEstado(validMateria.getId(), validMateria.getEstado()))
                .willReturn(Optional.ofNullable(validMateria));

        MateriaRecord mr = target.findById(validMateria.getId());
        assertThat(mr).isOfAnyClassIn(MateriaRecord.class)
                .hasFieldOrPropertyWithValue("id", validMateria.getId())
                .hasFieldOrPropertyWithValue("nombre", StringUtils.capitalize(validMateria.getNombre().toLowerCase()));
    }

    @Test
    void getById_return_not_found() {
        given(mockMateriaRepository.findByIdAndEstado(validMateria.getId(), validMateria.getEstado()))
                .willReturn(Optional.empty());
        int materiaId = validMateria.getId();
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.findById(materiaId);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Materia no encontrada");

    }
}