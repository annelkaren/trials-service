package mx.gob.pjpuebla.trials.core.rubros;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RubroResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class RubroResourceTest {

    @MockBean
    private RubroService mockRubroService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllByProcedimiento() throws Exception {
        RubroRecord rubroRecord = new RubroRecord(1, "Rubro 1");

        given(mockRubroService.getAllByProcedimiento(any()))
                .willReturn(Collections.singletonList(rubroRecord));

        mockMvc.perform(
                        get("/api/core/rubro/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}