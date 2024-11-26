package mx.gob.pjpuebla.trials.core.paises;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaisResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class PaisResourceTest {

    @MockBean
    private PaisService paisService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPaisesSuccess() throws Exception {
        Pais pais1 = new Pais();
        pais1.setId(1);
        pais1.setCca2("MX");
        pais1.setNombreComun("México");

        Pais pais2 = new Pais();
        pais1.setId(2);
        pais2.setCca2("US");
        pais2.setNombreComun("Estados Unidos");

        List<PaisRecord> paisRecords = Arrays.asList(
                new PaisRecord(pais1.getCca2(), pais1.getNombreComun(), pais1.getId().toString()),
                new PaisRecord(pais2.getCca2(), pais2.getNombreComun(),pais1.getId().toString())
        );

        when(paisService.getAll()).thenReturn(paisRecords);

        mockMvc.perform(get("/api/core/paises")
                        .contentType("application/json"))
                .andExpect(status().isOk());

    }
}