package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AudienciaPruebasResource.class)
@AutoConfigureMockMvc(addFilters = false)
class AudienciaPruebasResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AudienciaPruebasService audienciasPruebasService;

    @Test
    void createAudienciaPrueba_ShouldReturnOk() throws Exception {
        // Preparar datos de prueba
        AudienciaPruebaRequestRecord audienciaPruebaRequest = new AudienciaPruebaRequestRecord(
                1,
                2,
                "John Doe",
                "Descripción del instrumento",
                "Absolvente", // absolvente
                "Descripción del documento",
                "Objeto del documento", // objeto
                "http://example.com/documento.pdf",
                1001
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
}
