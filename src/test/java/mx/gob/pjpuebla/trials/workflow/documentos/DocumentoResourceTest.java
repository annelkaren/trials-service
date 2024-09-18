package mx.gob.pjpuebla.trials.workflow.documentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DocumentoResourceTest {

    @MockBean
    private DocumentoService documentoService;

    @MockBean
    private SelloGenerator selloGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_demanda() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1)).setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(1, demanda.getFolio(), TipoDocumento.DEMANDA);

        given(documentoService.createDemanda(any(DocumentoDTO.class)))
                .willReturn(documentoRecord);

        mockMvc.perform(
                post("/api/workflow/demanda")
                        .content(asJsonString(documentoRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
