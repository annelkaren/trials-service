package mx.gob.pjpuebla.trials.core.tipooficialias;


import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TipoOficialiaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TipoOficialiaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoOficialiaService tipoOficialiaService;

    private TipoOficialiaRecord validTipoOficialiaRecord;

    @BeforeEach
    void setUp() {
        validTipoOficialiaRecord = TipoOficialiaSetUp.createTipoOficialiaRecord();
    }

    @Test
    void getAll_success() throws Exception {
        given(tipoOficialiaService.getAll(any(Pageable.class), any(TipoOficialia.class)))
                .willReturn(Collections.singletonList(validTipoOficialiaRecord));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/core/tipooficialia")
                        .param("tipoOficialiaNombre", "TipoOficialia Status")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

}