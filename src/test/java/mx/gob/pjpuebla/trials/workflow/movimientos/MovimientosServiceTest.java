package mx.gob.pjpuebla.trials.workflow.movimientos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.util.UUID;
import java.util.Date;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class MovimientosServiceTest {

    @Mock
    MovimientoRepository movimientoRepository;

    @InjectMocks
    MovimientoService movimientoService;

    MovimientoSalidaRecord movimiento;
    UUID uuid;

    EstadoCarpeta estadoCarpeta;

    @BeforeEach
    public void setUp() {
        String uuidMov = "d8945bc4-af8e-4eb0-b742-7ee13beb43e0";
        uuid = UUID.fromString(uuidMov);
        estadoCarpeta = EstadoCarpeta.SALIDA;

        movimiento = new MovimientoSalidaRecord(uuid, TipoCarpeta.DEMANDA, "1", "00001/2024", new Date(), "Juzgado 1", null);
    }

    @Test
    void getMovimientosSalidasTest(){
        List<MovimientoSalidaRecord> movimientos = List.of(movimiento);

        given(movimientoRepository.salidas(uuid, estadoCarpeta)).willReturn(movimientos);

        movimientos = movimientoService.getMovimientosSalida(uuid.toString());

        assertThat(movimientos)
            .isNotEmpty()
            .anyMatch(mov -> mov.uuid().equals(uuid));
    }

}
