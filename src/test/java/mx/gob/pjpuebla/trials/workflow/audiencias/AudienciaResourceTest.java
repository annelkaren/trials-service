package mx.gob.pjpuebla.trials.workflow.audiencias;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.CatalogoMotivosRetrasoAudiencias;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.*;
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
                patch("/api/workflow/bandeja/audienciasgenerales/diferir/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createAudiencia() throws Exception {
        AudienciaSaveRecord audiencia = AudienciaSetUp.audienciaSaveRecordCreate();
        mockMvc.perform(
                post("/api/workflow/audiencias/crearAudiencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(audiencia)))
                .andExpect(status().isOk());
        
    }

    @Test
    void getEstatusAudiencias() throws Exception {
        List<String> estatusList = Arrays.asList("PROGRAMADA", "DESAHOGADA");

        given(audienciaService.getEstatusAudiencias()).willReturn(estatusList);

        mockMvc.perform(get("/api/workflow/audiencias/estatus")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void setHoraAudiencias() throws Exception {

        mockMvc.perform(post("/api/workflow/audiencias/horaInicio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idAudiencia\":1,\"hora\":\"2024-11-27T10:00:00\",\"isInicio\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void audienciaTabGeneral_success() throws Exception {
        AudienciaTabGeneralRecord audienciaTabGeneralRecord = AudienciaSetUp.createAudienciaTabGeneralRecord();

        mockMvc.perform(patch("/api/workflow/audiencias/tabGeneral")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(audienciaTabGeneralRecord)))
                .andExpect(status().isOk());
    }

    @Test
    void reprogramarAudiencia_success() throws Exception {
        given(audienciaService.reprogramarAudiencia(any(ReprogramarAudienciaRecord.class)))
                .willReturn(AudienciaSetUp.createAudienciasResponseRecord());

        mockMvc.perform(patch("/api/workflow/bandeja/audienciasgenerales/reprogramar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(AudienciaSetUp.createReprogramarAudienciaRecord())))
                .andExpect(status().isOk());
    }

    @Test
    void getAgenda() throws Exception {
        mockMvc.perform(
                get("/api/workflow/audiencias/getAgenda/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

        @Test
        void validarDisponibilidad_success() throws Exception {
                ValidarDisponibilidadRequestRecord request = new ValidarDisponibilidadRequestRecord(
                        1L,"2024-12-20", "10:00:00", 60
                );

                when(audienciaService.validarDisponibilidad(request)).thenReturn(true);

                mockMvc.perform(post("/api/workflow/audiencias/validarDisponibilidad")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ResourceUtilTest.asJsonString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().string("La sala está disponible"));
        }

        @Test
        void validarDisponibilidad_conflict() throws Exception {
                ValidarDisponibilidadRequestRecord request = new ValidarDisponibilidadRequestRecord(
                        1L, "2024-12-20", "10:00:00", 60
                );

                when(audienciaService.validarDisponibilidad(request)).thenReturn(false);
                mockMvc.perform(post("/api/workflow/audiencias/validarDisponibilidad")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ResourceUtilTest.asJsonString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Audiencia en conflicto"));
        }

}