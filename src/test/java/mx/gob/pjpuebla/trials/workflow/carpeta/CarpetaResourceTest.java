package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
                        .param("numExpediente", "000001")
                        .param("year", "2024")
                        .param("idJuzgado", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPersonaDocumentoById_success() throws Exception {
        List<ApelacionRecordResponse> expectedResponses = Collections.singletonList(apelacionRecordResponse);

        given(mockCarpetaService.getPersonasDocumentoByCarpetaId(anyInt()))
                .willReturn(expectedResponses);

        mockMvc.perform(
                get("/api/workflow/carpeta/personas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerBandejaRecepcionSuccess() throws Exception {

        String folio = "1";
        BandejaRecepcionRecord expectedRecord = new BandejaRecepcionRecord(folio, "Expediente 1", TipoCarpeta.DEMANDA,
                "ruta/digitalizacion", List.of());

        when(mockCarpetaService.getBandejaRecepcionByFolio(folio)).thenReturn(expectedRecord);

        // Realizar la llamada al endpoint y verificar el resultado
        mockMvc.perform(get("/api/workflow/carpeta/recepcion")
                .param("folio", folio)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.expediente").value("Expediente 1"));
    }

    @Test
    void obtenerBandejaRecepcionFail() throws Exception {

        String folio = "A";
        BandejaRecepcionRecord expectedRecord = new BandejaRecepcionRecord(folio, "Expediente 1", TipoCarpeta.DEMANDA,
                "ruta/digitalizacion", List.of());

        when(mockCarpetaService.getBandejaRecepcionByFolio(folio)).thenReturn(expectedRecord);

        // Realizar la llamada al endpoint y verificar el resultado
        mockMvc.perform(get("/api/workflow/carpeta/recepcion")
                .param("folio", folio)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.expediente").value("Expediente 1"));
    }

    @Test
    void obtenerBandejaRecepcionNotFound() throws Exception {
        String folio = "999";

        when(mockCarpetaService.getBandejaRecepcionByFolio(folio))
                .thenThrow(new NotFoundException("No se encontró la carpeta con el folio: " + folio, folio));

        mockMvc.perform(get("/api/workflow/carpeta/recepcion")
                .param("folio", folio)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}