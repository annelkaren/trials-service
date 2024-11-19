package mx.gob.pjpuebla.trials.core.rubros;

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
class RubroServiceTest {

    @InjectMocks
    RubroService target;

    @Mock
    private RubroRepository rubroRepository;

    @Test
    void getAllByProcedimiento() {
        Rubro rubro =  new Rubro();
        rubro.setId(1).setNombre("Procedimiento 1");

        given(rubroRepository.findByProcedimientoIdAndEstado(any(), any()))
                .willReturn(Collections.singletonList(rubro));

        List<RubroRecord> result = target.getAllByProcedimiento(1);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}