package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonaDocumentoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PersonaDocumentoResourceTest {

    @MockBean
    PersonaDocumentoService personaDocumentoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTipoPartesPrincipales() throws Exception{
        List<PersonaDocumentoNameRecord> personas = List.of(new PersonaDocumentoNameRecord(1, "Juan Pérez"));

        given(personaDocumentoService.getTipoPartesPrincipales(anyInt(), anyString())).willReturn(personas);

        mockMvc.perform(get("/api/workflow/personasdocumentos/51/actor")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
