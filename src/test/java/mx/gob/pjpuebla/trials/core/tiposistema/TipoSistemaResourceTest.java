package mx.gob.pjpuebla.trials.core.tiposistema;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TipoSistemaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TipoSistemaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TipoSistemaService tipoSistemaService;

    private TipoSistemaRecord validTipoSistemaRecord;

    @BeforeEach
    void setUp() {
        validTipoSistemaRecord = TipoSistemaSetUp.createTipoSistemaRecord();
    }

    @Test
    void getAll_success() throws Exception {
        given(tipoSistemaService.getAll(any(Pageable.class), any(TipoSistema.class)))
                .willReturn(Collections.singletonList(validTipoSistemaRecord));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/core/tiposistema")
                        .param("tiposistemaNombre", "TipoSistema Status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}