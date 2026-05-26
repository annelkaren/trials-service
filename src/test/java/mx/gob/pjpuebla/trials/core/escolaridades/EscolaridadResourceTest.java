package mx.gob.pjpuebla.trials.core.escolaridades;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EscolaridadResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EscolaridadResourceTest {

    @MockitoBean
    private EscolaridadService escolaridadService;

    @Autowired
    private MockMvc mockMvc;

    private EscolaridadRecord escolaridad;

    @BeforeEach
    void setUp() {
        escolaridad = EscolaridadSetUp.createEscolaridadRecord();
    }

    @Test
    void getAllByActive_success() throws Exception {
        given(escolaridadService.getAllActive(any(Pageable.class), any(Escolaridad.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(escolaridad)));

        mockMvc.perform(
                get("/api/core/escolaridades")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
