package mx.gob.pjpuebla.trials.workflow.audiencias;

import jakarta.ws.rs.core.MediaType;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AudienciaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AudienciaResourceTest {

    @MockBean
    private AudienciaService audienciaService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllAudienciasGenerales() throws Exception {
        AudienciasGeneralesResponseRecord audienciaRecord = AudienciaSetUp.createAudienciasGeneralesResponseRecord();

        given(audienciaService.getAllAudienciasGenerales(anyString(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(audienciaRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/audienciasgenerales")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/workflow/bandeja/audienciasgenerales/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}