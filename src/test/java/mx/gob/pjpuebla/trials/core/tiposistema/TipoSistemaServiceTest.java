package mx.gob.pjpuebla.trials.core.tiposistema;


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


import static mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp.CreateTipoSistema;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TipoSistemaServiceTest {

    @Mock
    public TipoSistemaRepository mocktipoSistemaRepository;

    @InjectMocks
    public TipoSistemaService target;

    private TipoSistema validTipoSistema;

    @BeforeEach
    public void setUp() {
        validTipoSistema = CreateTipoSistema();
    }

    @Test
    void getAll_return_list() {
        List<TipoSistema> listPage = Collections.singletonList(validTipoSistema);
        Page<TipoSistema> page = new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size());

        given(mocktipoSistemaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(page);
        List<TipoSistemaRecord> resultList = target.getAll(PageRequest.of(0, 1), validTipoSistema);

        assertThat(resultList)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", validTipoSistema.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoSistema.getNombre());
    }

    @Test
    void getAll_return_not_found() {
        when(mocktipoSistemaRepository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> target.getAll(PageRequest.of(0, 1), new TipoSistema())
        );

        assertThat(exception.getMessage()).contains("Tipo Sistemas no encontrados");
    }
}