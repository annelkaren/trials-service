package mx.gob.pjpuebla.trials.core.eventos;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoEditRecord;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoPeriodosRecord;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EventoResourceTest {

    @MockBean
    private EventoService mockEventoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPeriodos_success() throws Exception {
        EventoPeriodosRecord requestRecord = EventoSetUp.eventoPeriodosRecord();
        Evento eventoResponse = EventoSetUp.createEvento();

        given(mockEventoService.createPeriodos(any(EventoPeriodosRecord.class)))
                .willReturn(eventoResponse);

        mockMvc.perform(post("/api/core/evento/periodos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(requestRecord)))
                .andExpect(status().isOk());
    }

    @Test
    void getEventosGenerales_success() throws Exception {
        Page<EventoRecord> eventoPage = new PageImpl<>(List.of(EventoSetUp.eventoRecord()));

        given(mockEventoService.getEventosGenerales(PageRequest.of(0, 10)))
                .willReturn(eventoPage);

        mockMvc.perform(get("/api/core/evento/generales")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventosOficialiaComun_success() throws Exception {
        Page<EventoRecord> eventoPage = new PageImpl<>(List.of(EventoSetUp.eventoRecord()));

        given(mockEventoService.getEventosOficialiaComun(PageRequest.of(0, 10)))
                .willReturn(eventoPage);

        mockMvc.perform(get("/api/core/evento/oficialiacomun")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(
                delete("/api/core/evento/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void editarEventoPeriodo_success() throws Exception {
        EventoEditRecord requestRecord = EventoSetUp.eventoEditRecord();
        EventoRecord eventoRecordResponse = EventoSetUp.eventoRecord();

        given(mockEventoService.editarEventoPeriodo(any(EventoEditRecord.class)))
                .willReturn(eventoRecordResponse);

        mockMvc.perform(put("/api/core/evento/editarperiodo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(requestRecord)))
                .andExpect(status().isOk());
    }
}