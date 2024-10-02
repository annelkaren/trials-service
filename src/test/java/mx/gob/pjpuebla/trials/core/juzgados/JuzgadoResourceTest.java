package mx.gob.pjpuebla.trials.core.juzgados;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JuzgadoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class JuzgadoResourceTest {

    @MockBean
    private JuzgadoService mockJuzgadoService;
    @MockBean
    private JuzgadoUpdateValidator mockJuzgadoUpdateValidator;

    @Autowired
    private MockMvc mockMvc;

    private Juzgado juzgado;
    private JuzgadoRecord juzgadoRecord;
    private JuzgadoRecordItem juzgadoRecordItem;

    @BeforeEach
    void setUp() {
        Materia materia = MateriaSetUp.createMateria();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgadoRecord = JuzgadoSetUp.createJuzgadoRecord(juzgado, materia.getId(), sede.getId());
        juzgadoRecordItem = JuzgadoSetUp.createJuzgadoRecordResponse(juzgado, materia.getNombre());
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockJuzgadoService.getAll(any(Juzgado.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(juzgadoRecordItem)));

        mockMvc.perform(
                get("/api/core/juzgados")
                        .param("nombre", "J")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockJuzgadoService.findById(anyInt()))
                .willReturn(juzgadoRecord);

        mockMvc.perform(
                        get("/api/core/juzgados/1")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("nombre").value(juzgadoRecord.nombre()));
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockJuzgadoService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/juzgados/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockJuzgadoService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/juzgados/X")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        given(mockJuzgadoService.create(juzgado))
                .willReturn(juzgadoRecordItem);

        mockMvc.perform(
                post("/api/core/juzgados")
                        .content(ResourceUtilTest.asJsonString(juzgado))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void create_error() throws Exception {
        given(mockJuzgadoService.create(juzgado))
                .willReturn(juzgadoRecordItem);
        juzgado.setNombre("12");
        mockMvc.perform(
                        post("/api/core/juzgados")
                                .content(ResourceUtilTest.asJsonString(juzgado))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("[0].field").value("nombre"))
                .andExpect(jsonPath("[0].message").value("size must be between 3 and 250"));
    }

    @Test
    void update_success() throws Exception {
        given(mockJuzgadoUpdateValidator.supports(any()))
                .willReturn(true);
        given(mockJuzgadoService.create(juzgado))
                .willReturn(juzgadoRecordItem);

        mockMvc.perform(
                put("/api/core/juzgados")
                        .content(ResourceUtilTest.asJsonString(juzgado))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        given(mockJuzgadoUpdateValidator.supports(any()))
                .willReturn(true);
        given(mockJuzgadoService.update(any(Juzgado.class)))
                .willThrow(InvalidVersionException.class);

        mockMvc.perform(
                put("/api/core/juzgados")
                        .content(ResourceUtilTest.asJsonString(juzgado))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/core/juzgados/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
