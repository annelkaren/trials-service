package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import mx.gob.pjpuebla.trials.util.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TipoPartesServiceTest {

    @Mock
    TipoPartesRepository mockTipoPartesRepository;

    @InjectMocks
    TipoPartesService target;

    private TipoPartes validTipoPartes;

    @BeforeEach
    public void setUp() {
        validTipoPartes = createTipoPartes();
    }

   @Test
    void getAll_return_page() {
        List<TipoPartes> listPage = Collections.singletonList(validTipoPartes);
        given(mockTipoPartesRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<TipoPartesRecord> page = target.getAll(PageRequest.of(1, listPage.size()), validTipoPartes);
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre());
    }

    @Test
    void getById_return_tipoPartes() {
        given(mockTipoPartesRepository.findById(validTipoPartes.getId()))
                .willReturn(Optional.ofNullable(validTipoPartes));

        TipoPartesRecord mr = target.findById(validTipoPartes.getId());
        assertThat(mr).isOfAnyClassIn(TipoPartesRecord.class)
                .hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre());
    }

    @Test
    void findByIdError() {
        given(mockTipoPartesRepository.findById(validTipoPartes.getId()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.findById(validTipoPartes.getId());
                }
        );

        assertThat(assertThrows.getMessage()).contains("TipoPartes no encontrada");
    }

    private TipoPartes createTipoPartes() {
        return new TipoPartes()
                .setId(new Random().nextInt())
                .setEstado(Estado.ACTIVE)
                .setNombre(RandomStringUtils.random(5, true, true));
    }
}
