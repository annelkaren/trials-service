package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.gob.pjpuebla.trials.util.enums.DesistimientoAdmision;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.DetallesPruebasRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AudienciaPruebasResource.class)
@AutoConfigureMockMvc(addFilters = false)
class AudienciaPruebasResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AudienciaPruebasService audienciasPruebasService;

    @Test
    void createAudienciaPrueba_ShouldReturnOk() throws Exception {
        AudienciaPruebaRequestRecord audienciaPruebaRequest = new AudienciaPruebaRequestRecord(
                1,
                2,
                "John Doe",
                "Descripción del instrumento",
                "Absolvente", // absolvente
                "Descripción del documento",
                "Objeto del documento", // objeto
                "http://example.com/documento.pdf",
                1001,
                1 // Assuming 1 as a default value for the missing 10th Integer argument
        );
        String audienciaPruebaRequestJson = new ObjectMapper().writeValueAsString(audienciaPruebaRequest);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "contenido de prueba".getBytes()
        );

        MockMultipartFile audienciaPruebaRequestPart = new MockMultipartFile(
                "audienciaPruebaRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                audienciaPruebaRequestJson.getBytes()
        );

        AudienciaPruebas expectedResult = new AudienciaPruebas();
        expectedResult.setNombreDeclarante("John Doe");

        Mockito.when(audienciasPruebasService.createAudienciaPrueba(
                Mockito.any(AudienciaPruebaRequestRecord.class),
                Mockito.any(MultipartFile.class)))
            .thenReturn(expectedResult);

        mockMvc.perform(multipart("/api/workflow/audiencias-pruebas")
                        .file(audienciaPruebaRequestPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void updateDesistimientoAdmision_ShouldReturnOk() throws Exception {
        String desAdm = "DESISTIMIENTO"; // Simula el estado a actualizar
        Integer id = 1; // ID de la audiencia

        Mockito.doNothing().when(audienciasPruebasService).pachDesistimientoAdmision(Mockito.anyString(), Mockito.anyInt());

        mockMvc.perform(post("/api/workflow/audiencias-pruebas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(desAdm))
                .andExpect(status().isOk());

        Mockito.verify(audienciasPruebasService, Mockito.times(1))
                .pachDesistimientoAdmision(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    void getFile_ShouldReturnPdfFile() throws Exception {
        Integer id = 1;
        byte[] pdfBytes = new byte[] { 1, 2, 3, 4, 5 };

        Mockito.when(audienciasPruebasService.getAudienciaPurebasDocumento(Mockito.anyLong()))
                .thenReturn(pdfBytes);

        mockMvc.perform(get("/api/workflow/audiencias-pruebas/pruebaDoc/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByAudiencia_ShouldReturnDetails() throws Exception {
        Integer id = 1;
        List<DetallesPruebasRecord> detallesPruebasRecords = List.of(
                new DetallesPruebasRecord(1, "PersonaA", "Prueba A", "Descripción A", "N/A", DesistimientoAdmision.DESISTIMIENTO),
                new DetallesPruebasRecord(2, "PersonaB", "Prueba B", "Descripción B", "N/A", DesistimientoAdmision.ADMISION)
        );

        Page<DetallesPruebasRecord> pageMock = new PageImpl<>(detallesPruebasRecords);

        Mockito.when(audienciasPruebasService.getAudienciaPruebasByAudiencia(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(pageMock);

        mockMvc.perform(get("/api/workflow/audiencias-pruebas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)) // Verificamos que el contenido tiene 2 elementos
                .andExpect(jsonPath("$.content[0].asistente").value("PersonaA"))
                .andExpect(jsonPath("$.content[1].asistente").value("PersonaB"));
    }

}
