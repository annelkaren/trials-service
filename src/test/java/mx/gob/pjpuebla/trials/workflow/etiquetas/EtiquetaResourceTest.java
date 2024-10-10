package mx.gob.pjpuebla.trials.workflow.etiquetas;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EtiquetaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EtiquetaResourceTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    EtiquetaService etiquetaService;

    @Test
    void getAllByTipoJuicioId() throws Exception {
        Etiqueta item = EtiquetaSetUp.createEtiqueta(100);
        EtiquetaRecordItem record = new EtiquetaRecordItem(item.getNombre(), item.getValue());
        List<EtiquetaRecordItem> list = Arrays.asList(record);
        given(etiquetaService.getAllByTipoJuicioId(any())).willReturn(list);

        mockMvc.perform(
                get("/api/workflow/etiquetas/100")
                        .content(ResourceUtilTest.asJsonString(list))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
