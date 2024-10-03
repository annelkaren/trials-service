package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarpetaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CarpetaResourceTest {

    @MockBean
    private CarpetaService mockCarpetaService;

    @Autowired
    private MockMvc mockMvc;

    private CarpetaResponseRecord carpetaResponseRecord;
    private ApelacionRecordResponse apelacionRecordResponse;
    private DocumentoRecord documentoRecord;

    @BeforeEach
    void setUp() {
        carpetaResponseRecord = CarpetaSetUp.createCarpetaResponseRecord();
        apelacionRecordResponse = CarpetaSetUp.apelacionRecordResponse();
    }

    @Test
    void getCarpetaById_success() throws Exception {
        given(mockCarpetaService.getCarpetaResponseByNumExpYearJuzgado(anyString(), anyInt()))
                .willReturn(carpetaResponseRecord);
        mockMvc.perform(
                get("/api/workflow/carpeta")
                        .content(ResourceUtilTest.asJsonString(CarpetaSetUp.createCarpetaSearchRecord()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getPersonaDocumentoById_success() throws Exception {
        List<ApelacionRecordResponse> expectedResponses = Collections.singletonList(apelacionRecordResponse);

        given(mockCarpetaService.getPersonasDocumentoByCarpetaId(anyInt()))
                .willReturn(expectedResponses);

        mockMvc.perform(
                get("/api/workflow/carpeta/apelacion/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void create_apelacion_success() throws Exception {
        given(mockCarpetaService.createApelacion(CarpetaSetUp.apelacionRecord()))
                .willReturn(documentoRecord);

        mockMvc.perform(
                post("/api/workflow/carpeta/apelacion")
                        .content(ResourceUtilTest.asJsonString(BloqueSetUp.createBloque()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}