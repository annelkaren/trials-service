package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComentariosAsistentesResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ComentariosAsistentesResourceTest {

        @MockitoBean
        ComentariosAsistentesService comentariosAsistentesService;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void getComentariosByPersonaDocumento_success() throws Exception {
                given(comentariosAsistentesService.getComentariosByPersonaDocumento(eq(112), any(Pageable.class)))
                                .willReturn(new PageImpl<>(Collections.singletonList(
                                                ComentariosAsistentesSetUp.comentariosAsistentesResponse())));

                mockMvc.perform(
                                get("/api/workflow/comentariosasistentes/comentariopersona/112")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void createComentario_success() throws Exception {
                given(comentariosAsistentesService.create(any(ComentariosAsistentesRecord.class)))
                                .willReturn(ComentariosAsistentesSetUp.comentariosAsistentesResponse());

                mockMvc.perform(
                                post("/api/workflow/comentariosasistentes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(ComentariosAsistentesSetUp
                                                                .comentariosAsistentesRecord())))
                                .andExpect(status().isOk());
        }

        @Test
        void updateComentario_success() throws Exception {
                given(comentariosAsistentesService.update(eq(1), any(ComentariosAsistentesRecord.class)))
                                .willReturn(ComentariosAsistentesSetUp.comentariosAsistentesResponse());

                mockMvc.perform(
                                patch("/api/workflow/comentariosasistentes/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(
                                                                ComentariosAsistentesSetUp.comentariosAsistentes())))
                                .andExpect(status().isOk());
        }

}