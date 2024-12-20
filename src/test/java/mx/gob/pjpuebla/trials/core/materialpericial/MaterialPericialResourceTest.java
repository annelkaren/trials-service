package mx.gob.pjpuebla.trials.core.materialpericial;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaterialPericialResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MaterialPericialResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialPericialService materialPericialService;

    private MaterialPericial materialPericial;

    @BeforeEach
    void setUp() {
        materialPericial = MaterialPericialSetUp.createMaterialPericial();
    }

    @Test
    void getll_success() throws Exception {
        String nombre = "Documentoscopía y Grafoscopía";
        when(materialPericialService.getallMaterialParicial(nombre.toLowerCase()))
                .thenReturn(List.of(materialPericial));
        mockMvc.perform(get("/api/core/materialpericial")
                        .param("nombre", nombre)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}