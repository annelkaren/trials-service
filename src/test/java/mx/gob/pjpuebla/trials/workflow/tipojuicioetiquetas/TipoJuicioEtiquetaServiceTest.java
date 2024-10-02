package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TipoJuicioEtiquetaServiceTest {

    @Mock
    public TipoJuicioEtiquetaRepository tipoJuicioEtiquetaRepository;

    @InjectMocks
    public TipoJuicioEtiquetaService tipoJuicioEtiquetaService;

    private TipoJuicioEtiqueta tipoJuicioEtiqueta;

    @Test
    void getAllByTipoJuicioId() {
        TipoJuicioEtiqueta item = TipoJuicioEtiquetaSetUp.createTipoJuicioEtiqueta(100);
        List<TipoJuicioEtiqueta> list = Arrays.asList(item);
        given(tipoJuicioEtiquetaRepository.findByTipoJuicioId(anyInt())).willReturn(list);

        TipoJuicioEtiquetaItem record = new TipoJuicioEtiquetaItem(item.getNombre(), item.getValue());
        List<TipoJuicioEtiquetaItem> results = tipoJuicioEtiquetaService.getAllByTipoJuicioId(100);

        assertThat(results).isNotNull();
        assertThat(results.size()).isPositive();
        assertThat(results.get(0)).isEqualTo(record);
    }
}
