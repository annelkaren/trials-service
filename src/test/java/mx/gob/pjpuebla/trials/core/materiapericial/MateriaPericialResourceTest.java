package mx.gob.pjpuebla.trials.core.materiapericial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MateriaPericialResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MateriaPericialResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MateriaPericialService materiaPericialService;

    private MateriaPericial materiaPericial;

    @BeforeEach
    void setUp() {
        materiaPericial = MateriaPericialSetUp.createMateriaPericial();
    }

    @Test
    void getll_success() throws Exception {
        String nombre = "Documentoscopía y Grafoscopía";
        when(materiaPericialService.getallMateriaParicial(nombre.toLowerCase()))
                .thenReturn(List.of(materiaPericial));
        mockMvc.perform(get("/api/core/materiapericial")
                .param("nombre", nombre)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}