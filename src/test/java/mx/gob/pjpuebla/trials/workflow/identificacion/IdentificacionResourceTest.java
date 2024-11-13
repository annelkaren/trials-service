package mx.gob.pjpuebla.trials.workflow.identificacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdentificacionResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class IdentificacionResourceTest {

    @MockBean
    private IdentificacionService mockIdentificacionService;

    @Autowired
    private MockMvc mockMvc;

    private IdentificacionDocRecord identificacionDocRecord;

    @BeforeEach
    void setUp(){identificacionDocRecord = IdentificacionSetUp.createIdentificacionDoc() ;}

    @Test
    void getEtapaProcesal_success() throws Exception {
        given(mockIdentificacionService.getAll()).willReturn(Collections.singletonList(identificacionDocRecord));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/workflow/identificacion"))
                .andExpect(status().isOk());
    }
}