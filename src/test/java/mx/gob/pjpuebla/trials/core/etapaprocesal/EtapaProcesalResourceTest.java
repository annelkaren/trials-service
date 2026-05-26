package mx.gob.pjpuebla.trials.core.etapaprocesal;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.etapaprocesal.record.ListEtapaProcesalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EtapaProcesalResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EtapaProcesalResourceTest {

    @MockitoBean
    private EtapaProcesalService mocketapaProcesalService;

    @Autowired
    private MockMvc mockMvc;

    private ListEtapaProcesalRecord etapaProcesalRecord;

    @BeforeEach
    void setUp() {
        etapaProcesalRecord = EtapaProcesalSetUp.createListEtapaProcesalRecord();
    }

    @Test
    void getEtapaProcesal_success() throws Exception {
        given(mocketapaProcesalService.getEtapaProcesal(anyInt(), anyInt()))
                .willReturn(Collections.singletonList(etapaProcesalRecord));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/core/etapaprocesal")
                        .param("IdTipoJuicio", "150")
                        .param("IdProcedimiento", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}