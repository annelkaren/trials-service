package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import mx.gob.pjpuebla.trials.util.enums.DevolucionMotivo;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;

import static org.mockito.BDDMockito.willThrow;


@WebMvcTest(MovimientoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MovimientosResourceTest {
    @MockBean
    MovimientoService movimientoService;

    @MockBean
    MovimientoReporteGenerator generator;

    @MockBean
    AnexoRepository anexoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; 

    MovimientoSalidaRecord movimiento;
    UUID uuid;

    EstadoCarpeta estadoCarpeta;

    @BeforeEach
    public void setUp() {
        String uuidMov = "d8945bc4-af8e-4eb0-b742-7ee13beb43e0";
        uuid = UUID.fromString(uuidMov);
        estadoCarpeta = EstadoCarpeta.TURNADO;
        List<AnexoBandejaRecepcionRecord> anexos = new ArrayList<>();
        movimiento = new MovimientoSalidaRecord(uuid, TipoCarpeta.DEMANDA, "1", "00001/2024", LocalDateTime.now(), "Juzgado 1", null, "dasd", null, "expediente", "nombre", "persona", "observacion", 1, null, anexos);
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

    @Test
    void listaMotivosTest() throws Exception {
        Arrays.stream(DevolucionMotivo.values())
            .map(motivo -> new MotivoDevolucionRecord(motivo.getId(), motivo.getNombre()))
            .collect(Collectors.toList());
    
        mockMvc.perform(get("/api/workflow/movimientos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
  
    @Test
    void crearMotivoExitosoTest() throws Exception {
        MotivoRecord motivoRecord = new MotivoRecord("Error en en el numero de expediente", 1);

        mockMvc.perform(post("/api/workflow/movimientos/turnado")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(motivoRecord)))
            .andExpect(status().isOk());
    }

    @Test
    void crearMotivoErrorTest() throws Exception {
        MotivoRecord motivoRecord = new MotivoRecord("Error en el número de expediente", 1);

        willThrow(new RuntimeException("Error al crear el motivo")).given(movimientoService).createMotivo(motivoRecord);

        mockMvc.perform(post("/api/workflow/movimientos/turnado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motivoRecord)))
                .andExpect(status().isInternalServerError());
    }
}
