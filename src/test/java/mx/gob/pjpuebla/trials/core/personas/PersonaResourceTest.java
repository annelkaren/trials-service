package mx.gob.pjpuebla.trials.core.personas;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class PersonaResourceTest {

    @MockBean
    private PersonaService mockPersonaService;

    @Autowired
    private MockMvc mockMvc;

    private PersonaRecord validPersonaRecord;
    private PersonaRecordResponse personaRecordResponse;

    @BeforeEach
    void setUp() {
        personaRecordResponse = PersonaSetUp.createPersonaRecordResponse();
        validPersonaRecord = PersonaSetUp.createPersonaRecord();
    }

    @Test
    void getAll_success() throws Exception {
        given(mockPersonaService.getAll(any(Persona.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(personaRecordResponse)));

        mockMvc.perform(
                get("/api/core/personas")
                        .param("nombre", "J")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockPersonaService.findById(anyLong()))
                .willReturn(validPersonaRecord);

        mockMvc.perform(
                get("/api/core/personas/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockPersonaService.findById(anyLong()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/personas/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockPersonaService.findById(anyLong()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/personas/A")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        given(mockPersonaService.create(PersonaSetUp.createPersona(), List.of(roleRecord)))
                .willReturn(personaRecordResponse);

        mockMvc.perform(
                post("/api/core/personas")
                        .content(ResourceUtilTest.asJsonString(PersonaSetUp.createPersona()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        given(mockPersonaService.update(PersonaSetUp.createPersona(), List.of(roleRecord)))
                .willReturn(personaRecordResponse);

        mockMvc.perform(
                put("/api/core/personas")
                        .content(ResourceUtilTest.asJsonString(PersonaSetUp.createPersona()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        given(mockPersonaService.update(PersonaSetUp.createPersona(), List.of(roleRecord)))
                .willThrow(InvalidVersionException.class);

        mockMvc.perform(
                put("/api/core/personas")
                        .content(ResourceUtilTest.asJsonString(PersonaSetUp.createPersona()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getByCurp_success() throws Exception {
        String curp = "XXXX111111XXXXXX11";
        given(mockPersonaService.findByCurp(curp))
                .willReturn(validPersonaRecord);

        mockMvc.perform(
                get("/api/core/personas/curp/" + curp)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAll_jueces() throws Exception {
        JuezRecord juezRecord = new JuezRecord(1L, "Juan Perez");
        given(mockPersonaService.findAllJueces(any(Integer.class)))
                .willReturn(List.of(juezRecord));

        mockMvc.perform(
                get("/api/core/personas/jueces/" + anyInt())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAll_CentrosTrabajo() throws Exception {
        given(mockPersonaService.findAllCentroTrabajo(any(String.class)))
                .willReturn(List.of(new CentroTrabajoRecord(1, "TEST", TipoCentroTrabajo.JUZGADO)));

        mockMvc.perform(
                get("/api/core/personas/centrostrabajo")
                        .param("page", "0")
                        .param("size", "10")
                        .param("nombre", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAll_encargados_carrito() throws Exception {
        EncargadoCarritoRecord encargadoCarritoRecord = new EncargadoCarritoRecord(1L, "Juan Perez");
        given(mockPersonaService.findAllEncargadosCarrito())
                .willReturn(List.of(encargadoCarritoRecord));

        mockMvc.perform(
                get("/api/core/personas/encargadocarrito")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getPersonalTurnado_success() throws Exception {
        given(mockPersonaService.getPersonalTurnado())
                .willReturn(Collections.singletonList(personaRecordResponse));

        mockMvc.perform(
                get("/api/core/personas/turnado")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void verifyIfUserExistsAndIsLitigante_error() throws Exception {
        given(mockPersonaService.verifyIfUserExistsAndIsLitigante(any())).willReturn(true);

        mockMvc.perform(
                get("/api/core/personas/login")
                        .param("username", "test")
                        .param("password", "password")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void verifyIfUserExistsAndIsLitigante_error_isNotALitigante() throws Exception {
        given(mockPersonaService.verifyIfUserExistsAndIsLitigante(any())).willReturn(false);

        mockMvc.perform(
                get("/api/core/personas/login")
                        .param("username", "test")
                        .param("password", "password")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }
}