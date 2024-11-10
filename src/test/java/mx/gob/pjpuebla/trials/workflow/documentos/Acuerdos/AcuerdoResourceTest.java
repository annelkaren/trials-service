package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;

@WebMvcTest(AcuerdosResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AcuerdoResourceTest {

    @MockBean
    private AcuerdosService acuerdosService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crear_acuerdo() throws Exception {
        AcuerdoRecord acuerdo = AcuerdoRecordSetUp.create();

        given(acuerdosService.save(acuerdo)).willReturn(1);

        mockMvc.perform(
            post("/api/workflow/documentos/crearAcuerdo")
            .content(ResourceUtilTest.asJsonString(acuerdo))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
