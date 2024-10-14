package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

@WebMvcTest(MovimientoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class MovimientosResourceTest {
    @MockBean
    MovimientoService movimientoService;

    @MockBean
    MovimientoReporteGenerator generator;

    @Autowired
    private MockMvc mockMvc;

    MovimientoSalidaRecord movimiento;
    UUID uuid;

    EstadoCarpeta estadoCarpeta;

    @BeforeEach
    public void setUp() {
        String uuidMov = "d8945bc4-af8e-4eb0-b742-7ee13beb43e0";
        uuid = UUID.fromString(uuidMov);
        estadoCarpeta = EstadoCarpeta.TURNADO;

        movimiento = new MovimientoSalidaRecord(uuid, TipoCarpeta.DEMANDA, "1", "00001/2024", LocalDateTime.now(), "Juzgado 1", null, null);
    }

    @Test
    void getReporteSalidaTest() throws Exception{
        List<MovimientoSalidaRecord> movimientos = List.of(movimiento);
        byte[] reporte = new byte[2 * 1024 * 1024];

        given(movimientoService.getMovimientosSalida(uuid.toString())).willReturn(movimientos);
        given(generator.getReporteSalida(movimientos)).willReturn(reporte);

        mockMvc.perform(
                get("/api/workflow/movimientos/"+uuid.toString())
                        .accept(MediaType.APPLICATION_PDF))
        .andExpect(status().isOk());
    }

}
