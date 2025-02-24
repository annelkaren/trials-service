package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReasignacionExpedienteResponseRecord;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReactivacionExpedienteRecord;

import java.util.Map;

@WebMvcTest(ReasignacionExpedienteResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ReasignacionExpedienteResourceTest {

    @MockBean
    private ReasignacionExpedienteService reasignacionExpedienteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void reasignarExpediente_ShouldReturnResponseRecord() throws Exception {
        Map<String, Integer> requestData = Map.of("carpetaParentId", 1);
        ReasignacionExpedienteResponseRecord responseRecord =
                new ReasignacionExpedienteResponseRecord(10, 1, "Reasignación exitosa");

        given(reasignacionExpedienteService.reasignarExpediente(1)).willReturn(responseRecord);

        mockMvc.perform(post("/api/workflow/reasignacionExpediente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carpetaId").value(10))
                .andExpect(jsonPath("$.documentoId").value(1))
                .andExpect(jsonPath("$.response").value("Reasignación exitosa"));
    }

    @Test
    void reactivacionExpediente_ShouldReturnReactivacionRecord() throws Exception {
        ReactivacionExpedienteRecord record = new ReactivacionExpedienteRecord(1, "Reactivación exitosa");

        given(reasignacionExpedienteService.reactivacionExpediente(1)).willReturn(record);

        mockMvc.perform(patch("/api/workflow/reactivacionExpediente/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carpetaId").value(1))
                .andExpect(jsonPath("$.response").value("Reactivación exitosa"));
    }
}