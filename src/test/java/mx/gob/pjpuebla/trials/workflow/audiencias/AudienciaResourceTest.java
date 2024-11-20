package mx.gob.pjpuebla.trials.workflow.audiencias;

import jakarta.ws.rs.core.MediaType;

import mx.gob.pjpuebla.trials.util.enums.CatalogoMotivosRetrasoAudiencias;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoCondicionMigratoria;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AudienciaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AudienciaResourceTest {

    @MockBean
    private AudienciaService audienciaService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllAudienciasGenerales() throws Exception {
        AudienciasGeneralesResponseRecord audienciaRecord = AudienciaSetUp.createAudienciasGeneralesResponseRecord();

        given(audienciaService.getAllAudienciasGenerales(anyString(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(audienciaRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/audienciasgenerales")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/workflow/bandeja/audienciasgenerales/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAudienciasMotivos() throws Exception {
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoMotivosRetrasoAudiencias.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        when(audienciaService.getAudienciasMotivos())
                .thenReturn(items);

        mockMvc.perform(get("/api/workflow/audiencias/motivos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clave").value("ACTOR_NO_LLEGO"))
                .andExpect(jsonPath("$[0].etiqueta").value("La parte actora no llegó con la oportunidad solicitada"))
                .andExpect(jsonPath("$.length()").value(CatalogoMotivosRetrasoAudiencias.values().length));
    }

    @Test
    void diferir_success() throws Exception {
        mockMvc.perform(
                put("/api/workflow/bandeja/audienciasgenerales/diferir/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}