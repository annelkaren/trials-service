package mx.gob.pjpuebla.trials.core.oficialias;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecord;
import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.junit.jupiter.api.BeforeEach;
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

        @MockitoBean
        private OficialiaService mockOficialiaService;
        @MockitoBean
        private JuzgadoRepository juzgadoRepository;
        @MockitoBean
        private MateriaRepository materiaRepository;

        @Autowired
        private MockMvc mockMvc;

        private Oficialia oficialia;
        private OficialiaRecord validOficialiaRecord;
        private OficialiaRecordResponse oficialiaRecordResponse;
        private OficialiaMateriaRecord oficialiaMateriaRecordResponse;

        @BeforeEach
        void setUp() {
                Juzgado juzgado = JuzgadoSetUp.createJuzgado();
                TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
                Distrito distrito = DistritoSetUp.createDistrito();
                Domicilio domicilio = DomicilioSetUp.createDomicilio();
                Materia materia = MateriaSetUp.createMateria();
                Sede sede = SedeSetUp.createSede();
                sede.setDistrito(distrito);
                sede.setDomicilio(domicilio);
                oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);
                oficialia.setJuzgados(Collections.singletonList(juzgado)); // Usa una lista que contiene el juzgado
                validOficialiaRecord = OficialiaSetUp.createOficialiaRecord(oficialia,
                                new TipoOficialiaRecord(tipoOficialia.getId(), tipoOficialia.getNombre()),
                                new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado()));
                oficialiaMateriaRecordResponse = OficialiaSetUp.CreateOficialiaMateriaRecord(oficialia, materia, sede,
                                tipoOficialia, juzgado);
                oficialiaRecordResponse = OficialiaSetUp.createOficialiaRecordResponse(oficialia);
        }

        @Test
        void getAllByNameAndActive_success() throws Exception {
                given(mockOficialiaService.getAllByOficialiaMateria(null, null, null, null, null, null, null))
                                .willReturn(new PageImpl<OficialiaMateriaRecord>(
                                                Collections.singletonList(oficialiaMateriaRecordResponse)));

                mockMvc.perform(
                                get("/api/core/oficialias")
                                                .param("oficialiaName", "O")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getById_success() throws Exception {
                given(mockOficialiaService.findById(anyInt()))
                                .willReturn(validOficialiaRecord);

                mockMvc.perform(
                                get("/api/core/oficialias/1")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getById_not_found() throws Exception {
                given(mockOficialiaService.findById(anyInt()))
                                .willThrow(NotFoundException.class);

                mockMvc.perform(
                                get("/api/core/oficialias/0")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void getById_invalid() throws Exception {
                given(mockOficialiaService.findById(anyInt()))
                                .willThrow(MethodArgumentTypeMismatchException.class);

                mockMvc.perform(
                                get("/api/core/oficialias/X")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void create_success() throws Exception {
                given(mockOficialiaService.create(oficialia))
                                .willReturn(oficialiaRecordResponse);

                mockMvc.perform(
                                post("/api/core/oficialias")
                                                .content(ResourceUtilTest.asJsonString(oficialiaRecordResponse))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void update_success() throws Exception {
                given(mockOficialiaService.create(oficialia))
                                .willReturn(oficialiaRecordResponse);

                mockMvc.perform(
                                put("/api/core/oficialias")
                                                .content(ResourceUtilTest.asJsonString(oficialiaRecordResponse))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void update_error() throws Exception {
                given(mockOficialiaService.update(oficialia))
                                .willThrow(InvalidVersionException.class);

                mockMvc.perform(
                                put("/api/core/oficialias")
                                                .content(ResourceUtilTest.asJsonString(oficialiaRecordResponse))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void delete_success() throws Exception {
                mockMvc.perform(
                                delete("/api/core/oficialias/1")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }
}