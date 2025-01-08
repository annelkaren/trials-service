package mx.gob.pjpuebla.trials.core.derechoshumanos;


import mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos;
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


@WebMvcTest(DerechosHumanosResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DerechosHumanosResourceTest {

    @MockBean
    private DerechosHumanosService derechosHumanosService;

    @Autowired
    private MockMvc mockMvc;

    private DerechosHumanosRecord derechosHumanosRecord;

    @BeforeEach
    void setUp(){
        derechosHumanosRecord = new DerechosHumanosRecord(1,"Libertad de tránsito y residencia (Derecho a la)", TipoDerechosHumanos.DERECHOS_PERSONA);
    }

    @Test
    void getListByTipo_success() throws Exception {
        given(derechosHumanosService.getListByTipo("DERECHOS_PERSONA")).willReturn(Collections.singletonList(derechosHumanosRecord));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/core/derechosHumanos/DERECHOS_PERSONA"))
                .andExpect(status().isOk());
    }
}