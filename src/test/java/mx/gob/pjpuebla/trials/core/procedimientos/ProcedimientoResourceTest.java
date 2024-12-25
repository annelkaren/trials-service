package mx.gob.pjpuebla.trials.core.procedimientos;

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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcedimientoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ProcedimientoResourceTest {

    @MockBean
    private ProcedimientoService mockProcedimientoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllByTipoJuicio() throws Exception {
        ProcedimientoRecord procedimientoRecord =  new ProcedimientoRecord(1, "Procedimiento 1");

        given(mockProcedimientoService.getAllByTipoJuicio(anyInt()))
                .willReturn(Collections.singletonList(procedimientoRecord));

        mockMvc.perform(
                        get("/api/core/procedimiento/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}