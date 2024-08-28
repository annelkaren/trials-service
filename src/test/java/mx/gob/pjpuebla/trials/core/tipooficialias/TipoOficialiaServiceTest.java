package mx.gob.pjpuebla.trials.core.tipooficialias;

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


import static mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp.CreatetipoOficialia;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TipoOficialiaServiceTest {

    @Mock
    public TipoOficialiasRepository mockTipoOficialiasRepository;

    @InjectMocks
    public TipoOficialiaService target;

    private TipoOficialias validTipoOficialias;

    @BeforeEach
    public void setUp() {
        validTipoOficialias = CreatetipoOficialia();
    }

    @Test
    void getAll_return_list() {
        List<TipoOficialias> listPage = Collections.singletonList(validTipoOficialias);
        Page<TipoOficialias> page = new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size());

        given(mockTipoOficialiasRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(page);

        List<TipoOficialiaRecord> resultList = target.getAll(PageRequest.of(0, 1), validTipoOficialias);

        assertThat(resultList)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", validTipoOficialias.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoOficialias.getNombre());
    }

    @Test
    void getAll_return_not_found() {
        when(mockTipoOficialiasRepository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> target.getAll(PageRequest.of(0, 1), new TipoOficialias())
        );

        assertThat(exception.getMessage()).contains("Tipo Oficialias no encontrados");

    }
}