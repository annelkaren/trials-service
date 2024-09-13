package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
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
        validTipoPartes = TipoPartesSetUp.createTipoPartes();
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        Materia materia = MateriaSetUp.createMateria();
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        validTipoPartes.setTipoJuicio(tipoJuicio);
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
        int id = validTipoPartes.getId();
        given(mockTipoPartesRepository.findById(validTipoPartes.getId()))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> target.findById(id)
        );

        assertThat(assertThrows.getMessage()).contains("TipoPartes no encontrada");
    }

    @Test
    void getByTipoJuicioId_return_tipoPartes() {
        given(mockTipoPartesRepository.findByTipoJuicioId(validTipoPartes.getTipoJuicio().getId()))
                .willReturn(Collections.singletonList(validTipoPartes));

        List<TipoPartesRecord> list = target.findByTipoJuicioId(validTipoPartes.getTipoJuicio().getId());
        assertThat(list).hasSize(1);
        assertThat(list.get(0))
                .hasFieldOrPropertyWithValue("id", validTipoPartes.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoPartes.getNombre())
                .hasFieldOrPropertyWithValue("tipoJuicio", validTipoPartes.getTipoJuicio().getNombre());
    }

}
