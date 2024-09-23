package mx.gob.pjpuebla.trials.core.oficialias;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OficialiaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class OficialiaResourceTest {

    @MockBean
    private OficialiaService mockOficialiaService;

    @Autowired
    private MockMvc mockMvc;

    private Oficialia oficialia;
    private OficialiaRecord validOficialiaRecord;
    private OficialiaRecordResponse oficialiaRecordResponse;

    @BeforeEach
    void setUp() {
        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);
        validOficialiaRecord = OficialiaSetUp.createOficialiaRecord(oficialia, new TipoOficialiaRecord(tipoOficialia.getId(), tipoOficialia.getNombre()), new SedeRecordResponse(sede.getId(),sede.getNombre(),sede.getEstado()));
        oficialiaRecordResponse =  OficialiaSetUp.createOficialiaRecordResponse(oficialia);
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockOficialiaService.getAllActive(any(Pageable.class), any(Oficialia.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validOficialiaRecord)));

        mockMvc.perform(
                get("/api/core/oficialias")
                        .param("oficialiaName", "O")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockOficialiaService.findById(anyInt()))
                .willReturn(validOficialiaRecord);

        mockMvc.perform(
                get("/api/core/oficialias/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockOficialiaService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/oficialias/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockOficialiaService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/oficialias/X")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        given(mockOficialiaService.create(oficialia))
                .willReturn(oficialiaRecordResponse);

        mockMvc.perform(
                post("/api/core/oficialias")
                        .content(asJsonString(oficialiaRecordResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        given(mockOficialiaService.create(oficialia))
                .willReturn(oficialiaRecordResponse);

        mockMvc.perform(
                put("/api/core/oficialias")
                        .content(asJsonString(oficialiaRecordResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        given(mockOficialiaService.update(oficialia))
                .willThrow(InvalidVersionException.class);

        mockMvc.perform(
                put("/api/core/oficialias")
                        .content(asJsonString(oficialiaRecordResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/core/oficialias/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}