package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DesahogoAudienciaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DesahogoAudienciaResourceTest {

    @MockitoBean
    private DesahogoAudienciaService desahogoAudienciaService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPaisesSuccess() throws Exception {
        DesahogoAudiencia desAu1 = new DesahogoAudiencia();
        desAu1.setId(1);
        desAu1.setKey("COM_POR_CO");
        desAu1.setNombre("Conclusión por Convenio");

        DesahogoAudiencia desAu2 = new DesahogoAudiencia();
        desAu2.setId(2);
        desAu2.setKey("DES_DE_ACC");
        desAu2.setNombre("Desistimiento de la Acción");

        List<DesahogoAudienciaRecord> desahogoAudienciaRecords = Arrays.asList(
                new DesahogoAudienciaRecord(desAu1.getId(), desAu1.getKey(), desAu1.getNombre()),
                new DesahogoAudienciaRecord(desAu1.getId(), desAu1.getKey(), desAu1.getNombre()));

        when(desahogoAudienciaService.getAll()).thenReturn(desahogoAudienciaRecords);

        mockMvc.perform(get("/api/core/desahogoaudiencia")
                .contentType("application/json"))
                .andExpect(status().isOk());

    }

}