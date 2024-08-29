package mx.gob.pjpuebla.trials.core.tipoJuicio;

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

import static mx.gob.pjpuebla.trials.core.tipoJuicio.TipoJuicioSetUp.createTipoJuicio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TipoJuicioServiceTest {

    @Mock
    TipoJuicioRepository mockTipoJuicioRepository;

    @InjectMocks
    TipoJuicioService target;

    private TipoJuicio validTipoJuicio;

    @BeforeEach
    public void setUp() {
        validTipoJuicio = createTipoJuicio();
    }


    @Test
    void getAll_return_page() {
        List<TipoJuicio> listPage = Collections.singletonList(validTipoJuicio);
        given(mockTipoJuicioRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<TipoJuicioRecord> page = target.getAllActive(PageRequest.of(1, listPage.size()), validTipoJuicio);
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validTipoJuicio.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoJuicio.getNombre());
    }

    @Test
    void getById_return_tipojuicio() {
        given(mockTipoJuicioRepository.findByIdAndEstado(validTipoJuicio.getId(), validTipoJuicio.getEstado()))
                .willReturn(Optional.ofNullable(validTipoJuicio));

        TipoJuicioRecord tjr = target.findById(validTipoJuicio.getId());
        assertThat(tjr).isOfAnyClassIn(TipoJuicioRecord.class)
                .hasFieldOrPropertyWithValue("id", validTipoJuicio.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoJuicio.getNombre());
    }

    @Test
    void getById_return_not_found() {
        given(mockTipoJuicioRepository.findByIdAndEstado(validTipoJuicio.getId(), validTipoJuicio.getEstado()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.findById(validTipoJuicio.getId());
                }
        );

        assertThat(assertThrows.getMessage()).contains("Tipo de Juicio no encontrado");

    }

}