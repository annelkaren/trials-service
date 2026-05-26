package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.records.RegistrarAsistenciaAudienciaRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsistenciaAudienciaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AsistenciaAudienciaResourceTest {

        @MockitoBean
        AsistenciaAudienciaService asistenciaAudienciaService;

        @Autowired
        private MockMvc mockMvc;

        @Test
        void getAll() throws Exception {
                AsistenciaAudienciaResponse asistenciaAudienciaResponse = AsistenciaAudienciaSetUp
                                .asistenciaAudienciaResponse();

                given(asistenciaAudienciaService.getAll(any(Pageable.class)))
                                .willReturn(new PageImpl<>(Collections.singletonList(asistenciaAudienciaResponse)));
                mockMvc.perform(
                                get("/api/workflow/audienciaasistencia")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void registrarAsistencia_success() throws Exception {
                RegistrarAsistenciaAudienciaRecord record = new RegistrarAsistenciaAudienciaRecord(1, 1, 1);
                ObjectMapper objectMapper = new ObjectMapper();
                String registrarAsistenciaAudienciaRecordJson = objectMapper.writeValueAsString(record);

                MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf",
                                "contenido".getBytes());

                MockMultipartFile jsonFile = new MockMultipartFile(
                                "registrarAsistenciaAudienciaRecord",
                                "registrarAsistenciaAudienciaRecord.json",
                                "application/json",
                                registrarAsistenciaAudienciaRecordJson.getBytes());

                AsistenciaAudiencia asistenciaAudiencia = new AsistenciaAudiencia();
                asistenciaAudiencia.setId(1);

                given(asistenciaAudienciaService.registrarAsistencia(Mockito.any(), Mockito.any()))
                                .willReturn(asistenciaAudiencia);
                mockMvc.perform(multipart("/api/workflow/audienciaasistencia/registrarasistencia")
                                .file(file)
                                .file(jsonFile)
                                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE))
                                .andExpect(status().isOk());
        }

}