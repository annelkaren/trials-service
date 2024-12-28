package mx.gob.pjpuebla.trials.core.cuestionarios;

import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import mx.gob.pjpuebla.trials.util.enums.TipoPregunta;
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


@WebMvcTest(CuestionarioResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CuestionarioResourceTest {

    @MockBean
    private CuestionarioService cuestionarioService;

    @Autowired
    private MockMvc mockMvc;

    private CuestionarioRecord cuestionarioRecord;

    @BeforeEach
    void setUp(){cuestionarioRecord = new CuestionarioRecord(1, "¿La sentencia fue dictada por un órgano jurisdiccional auxiliar?", ListCuestionario.LISTA_SALAS, TipoPregunta.SI_NO );}

    @Test
    void getCuestionariosByLista_success() throws Exception {
        given(cuestionarioService.getListByLista(1)).willReturn(Collections.singletonList(cuestionarioRecord));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/core/cuestionario/1"))
                .andExpect(status().isOk());
    }

}