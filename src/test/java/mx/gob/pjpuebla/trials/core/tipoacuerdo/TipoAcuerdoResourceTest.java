package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TipoAcuerdoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TipoAcuerdoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoAcuerdoService tipoAcuerdoService;

    private TipoAcuerdoRecord tipoAcuerdoRecord;

    @BeforeEach
    void setUp() {
        tipoAcuerdoRecord = TipoAcuerdoSetUp.createTipoAcuerdoRecord();
    }

    @Test
    void getAll_success() throws Exception {

        given(tipoAcuerdoService.findByDocumentoId(1)).willReturn(Collections.singletonList(tipoAcuerdoRecord));
        mockMvc.perform(
                        get("/api/core/tipoacuerdo/{documentoId}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getAll_notFound() throws Exception {

        given(tipoAcuerdoService.findByDocumentoId(1)).willReturn(Collections.emptyList());
        mockMvc.perform(
                        get("/api/core/tipoacuerdo/{documentoId}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}