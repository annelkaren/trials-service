package mx.gob.pjpuebla.trials.core.nacionalidades;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NacionalidadResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class NacionalidadResourceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private NacionalidadService nacionalidadService;
    private NacionalidadRecord nacionalidad;

    @BeforeEach
    public void setUp() {
        nacionalidad = new NacionalidadRecord(1, "Nambiana");
    }

    @Test
    void getAll() throws Exception {
        given(nacionalidadService.getAll(any())).willReturn(Arrays.asList(nacionalidad));

        mockMvc.perform(
                get("/api/core/nacionalidades")
                        .param("key", "N")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
