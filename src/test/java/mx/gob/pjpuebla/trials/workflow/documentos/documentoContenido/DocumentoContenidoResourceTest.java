package mx.gob.pjpuebla.trials.workflow.documentos.documentoContenido;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoResource;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;

@WebMvcTest(DocumentoContenidoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DocumentoContenidoResourceTest {

    @MockBean
    private DocumentoContenidoService documentoContenidoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDataDocumentoDigitalizacion() throws Exception {
        given(documentoContenidoService.getDataDocumentoDigitalizacion(anyInt()))
                .willReturn(DocumentoContenidoSetUp.documentoOficioDigitalizacionRecordSetUp());

        mockMvc.perform(
                get("/api/workflow/documentoContenido/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cancelarOficio() throws Exception {
        given(documentoContenidoService.cancelarOficio(anyInt()))
                .willReturn(1);

        mockMvc.perform(
                patch("/api/workflow/documentoContenido/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateDocumentoOficioDigitalizacion() throws Exception {
        DocumentoOficioDigitalizacionRecord docRecord = DocumentoContenidoSetUp.documentoOficioDigitalizacionRecordSetUp();

        given(documentoContenidoService.updateDocumentoOficioDigitalizacion(docRecord))
                .willReturn(docRecord);

        mockMvc.perform(
                patch("/api/workflow/documentoContenido/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
