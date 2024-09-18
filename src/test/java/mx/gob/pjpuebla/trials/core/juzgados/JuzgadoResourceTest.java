package mx.gob.pjpuebla.trials.core.juzgados;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.*;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JuzgadoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class JuzgadoResourceTest {

    @MockBean
    private JuzgadoService mockJuzgadoService;

    @Autowired
    private MockMvc mockMvc;

    private Juzgado juzgado;
    private JuzgadoRecord juzgadoRecord;
    private JuzgadoRecordResponse juzgadoRecordResponse;
    private List<TipoJuicioRecord> tipoJuicioRecords = new ArrayList<>();

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
        juzgadoRecordResponse = JuzgadoSetUp.createJuzgadoRecordResponse(juzgado, materia.getNombre());
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockJuzgadoService.getAll(any(Juzgado.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(juzgadoRecordResponse)));

        mockMvc.perform(
                get("/api/core/juzgados")
                        .param("nombre", "J")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockJuzgadoService.findById(anyInt()))
                .willReturn(new JuzgadoDTO().setJuzgado(juzgado));

        mockMvc.perform(
                get("/api/core/juzgados/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
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
        tipoJuicioRecords.add(TipoJuicioSetUp.createTipoJuicioRecord());
        given(mockJuzgadoService.create(juzgado, tipoJuicioRecords))
                .willReturn(juzgadoRecordResponse);

        mockMvc.perform(
                post("/api/core/juzgados")
                        .content(asJsonString(SedeSetUp.createSede(Estado.ACTIVE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        tipoJuicioRecords.add(TipoJuicioSetUp.createTipoJuicioRecord());
        given(mockJuzgadoService.create(juzgado, tipoJuicioRecords))
                .willReturn(juzgadoRecordResponse);

        mockMvc.perform(
                put("/api/core/juzgados")
                        .content(asJsonString(SedeSetUp.createSede(Estado.ACTIVE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        tipoJuicioRecords.add(TipoJuicioSetUp.createTipoJuicioRecord());
        given(mockJuzgadoService.update(juzgado, tipoJuicioRecords))
                .willThrow(OptimisticLockingFailureException.class);

        mockMvc.perform(
                put("/api/core/juzgados")
                        .content(asJsonString(JuzgadoSetUp.createJuzgado(new Materia(), new Sede())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/core/juzgados/1")
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
