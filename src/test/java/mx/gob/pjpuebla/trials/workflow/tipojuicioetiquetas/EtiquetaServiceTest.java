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
class EtiquetaServiceTest {

    @Mock
    public EtiquetaRepository etiquetaRepository;

    @InjectMocks
    public EtiquetaService etiquetaService;

    @Test
    void getAllByTipoJuicioId() {
        Etiqueta item = EtiquetaSetUp.createEtiqueta(100);
        List<Etiqueta> list = Arrays.asList(item);
        given(etiquetaRepository.findByTipoJuicioId(anyInt())).willReturn(list);

        EtiquetaRecordItem record = new EtiquetaRecordItem(item.getNombre(), item.getValue());
        List<EtiquetaRecordItem> results = etiquetaService.getAllByTipoJuicioId(100);

        assertThat(results).isNotNull();
        assertThat(results.size()).isPositive();
        assertThat(results.get(0)).isEqualTo(record);
    }
}
