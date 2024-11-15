package mx.gob.pjpuebla.trials.core.documentoidentificacion;

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

@WebMvcTest(DocumentoIdentificacionResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DocumentoIdentificacionResourceTest {

    @MockBean
    private DocumentoIdentificacionService mockDocumentoIdentificacionService;

    @Autowired
    private MockMvc mockMvc;

    private IdentificacionDocRecord identificacionDocRecord;

    @BeforeEach
    void setUp(){identificacionDocRecord = DocumentoIdentificacionSetUp.createIdentificacionDoc() ;}

    @Test
    void getEtapaProcesal_success() throws Exception {
        given(mockDocumentoIdentificacionService.getAll()).willReturn(Collections.singletonList(identificacionDocRecord));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/core/documentoidentificacion"))
                .andExpect(status().isOk());
    }
}