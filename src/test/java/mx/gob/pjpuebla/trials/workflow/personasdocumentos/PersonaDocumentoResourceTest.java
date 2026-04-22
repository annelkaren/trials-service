package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonaDocumentoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PersonaDocumentoResourceTest {

    @MockitoBean
    PersonaDocumentoService personaDocumentoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTipoPartesPrincipales() throws Exception {
        List<PersonaDocumentoNameRecord> personas = List.of(new PersonaDocumentoNameRecord(1, "Juan Pérez"));

        given(personaDocumentoService.getTipoPartesPrincipales(anyInt(), anyString())).willReturn(personas);

        mockMvc.perform(get("/api/workflow/personasdocumentos/51/actor")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCorreoByPersonaDocumentoId_ShouldReturnCorreo() throws Exception {
        String correo = "juan.perez@example.com";
        given(personaDocumentoService.getCorreoByPersonaDocumentoId(1)).willReturn(correo);

        mockMvc.perform(get("/api/workflow/personasdocumentos/correo/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(correo));
    }

    @Test
    void getDomicilioByPersonaDocumentoId_ShouldReturnDomicilio() throws Exception {
        Domicilio mockDomicilio = new Domicilio();
        mockDomicilio.setCalle("Av. Principal");
        mockDomicilio.setCiudad("Puebla");
        mockDomicilio.setCodigoPostal("72000");
        mockDomicilio.setColonia("Centro");
        mockDomicilio.setEstadoRepublica("Puebla");
        mockDomicilio.setMunicipio("Puebla");
        mockDomicilio.setExterior("123");
        mockDomicilio.setInterior("456");

        given(personaDocumentoService.getDomicilioByPersonaDocumentoId(1)).willReturn(mockDomicilio);

        mockMvc.perform(get("/api/workflow/personasdocumentos/domicilio/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockDomicilio)));
    }

}
