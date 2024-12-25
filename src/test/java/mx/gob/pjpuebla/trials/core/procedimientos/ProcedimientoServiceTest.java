package mx.gob.pjpuebla.trials.core.procedimientos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProcedimientoServiceTest {

    @InjectMocks
    ProcedimientoService target;

    @Mock
    private ProcedimientoRepository procedimientoRepository;

    @Test
    void getAllByTipoJuicio() {
        Procedimiento procedimiento =  new Procedimiento();
        procedimiento.setId(1).setNombre("Procedimiento 1");

        given(procedimientoRepository.findByTipoJuicioIdAndEstado(any(), any()))
                .willReturn(Collections.singletonList(procedimiento));

        List<ProcedimientoRecord> result = target.getAllByTipoJuicio(1);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}