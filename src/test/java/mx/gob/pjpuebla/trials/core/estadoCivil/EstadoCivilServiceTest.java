package mx.gob.pjpuebla.trials.core.estadoCivil;

import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;

import static mx.gob.pjpuebla.trials.core.estadoCivil.EstadoCivilSetUp.createEstadoCivil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EstadoCivilServiceTest {

    @Mock
    private EstadoCivilRepository mockEstadoCivilRepository;

    @InjectMocks
    private EstadoCivilService target;

    private EstadoCivil validEstadoCivil;

    @BeforeEach
    public void setUp() {
        validEstadoCivil = createEstadoCivil();
    }

    @Test
    void getAll_return_list() {
        List<EstadoCivil> listPage = Collections.singletonList(validEstadoCivil);
        Page<EstadoCivil> page = new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size());

        given(mockEstadoCivilRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(page);

        List<EstadoCivilRecord> resultList = target.getAll(PageRequest.of(0, 1), validEstadoCivil);

        assertThat(resultList)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", validEstadoCivil.getId())
                .hasFieldOrPropertyWithValue("nombre", validEstadoCivil.getNombre());
    }

    @Test
    void getAll_return_not_found() {
        when(mockEstadoCivilRepository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> target.getAll(PageRequest.of(0, 1), new EstadoCivil())
        );

        assertThat(exception.getMessage()).contains("Estados Civiles no encontrados");
    }

}