package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvio;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnvioRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosCambioEstatus;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;

@WebMvcTest(BandejaEnviosResource.class)
@AutoConfigureMockMvc(addFilters = false)
public class BandejaEnviosResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BandejaEnviosService bandejaEnviosService;


    @Test
    public void testObtenerBandejaEnvios() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BandejaEnviosRecord> page = new PageImpl<>(Collections.emptyList());
        when(bandejaEnviosService.getAllBandejaEnviados("", pageable)).thenReturn(page);

        mockMvc.perform(get("/api/workflow/bandejaEnvios")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testActualizarEstatusOficio() throws Exception {
        BandejaEnvioRecordResponse response = new BandejaEnvioRecordResponse(1, "OK");
        BandejaEnviosCambioEstatus data = new BandejaEnviosCambioEstatus(List.of(1), EstadoEnvio.ENVIADO, null);
        when(bandejaEnviosService.actualizarEstatusOficio(data)).thenReturn(response);

        mockMvc.perform(patch("/api/workflow/bandejaEnvios/1/1"))
                .andExpect(status().isOk());
    }
}
