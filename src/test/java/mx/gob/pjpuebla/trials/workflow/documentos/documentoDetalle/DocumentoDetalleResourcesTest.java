package mx.gob.pjpuebla.trials.workflow.documentos.documentoDetalle;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleResources;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleService;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.files.DigitalizacionFolderSetup;

@WebMvcTest(DocumentoDetalleResources.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DocumentoDetalleResourcesTest {
    
    @MockBean
    private DocumentoDetalleService documentoDetalleService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void digitalizarAcuse() throws Exception {
        DocumentoDetalleRecord docDetalle = DocumentoDetalleSetUp.createDocumentoDetalleRecord();
        DigitalizacionRecord digitalizacion = DigitalizacionFolderSetup.getDigitalizacion();

        given(documentoDetalleService.digitalizacionAcuse(docDetalle))
            .willReturn(digitalizacion);

        mockMvc.perform(
            post("/api/workflow/documentoDetalle/digitalizar/acuse")
                .content(ResourceUtilTest.asJsonString(docDetalle))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    }

    @Test
    void getFile() throws Exception  {
        byte[] file = new byte[1];

        given(documentoDetalleService.getAcuse(anyInt()))
            .willReturn(file);
        
        mockMvc.perform(
            get("/api/workflow/documentoDetalle/digitalizar/acuse/1")
            .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}




