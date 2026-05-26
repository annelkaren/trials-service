package mx.gob.pjpuebla.trials.core.sedes;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SedeResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SedeResourceTest {

        @MockitoBean
        private SedeService mockSedeService;
        @MockitoBean
        private DomicilioRepository domicilioRepository;

        @Autowired
        private MockMvc mockMvc;

        private SedeRecordResponse sedeRecordResponse;
        private SedeDomiciliosRecord sedeDomiciliosRecord;
        private SedeRecord sedeRecord;
        private SedeDomicilioRecordResponse sedeDomicilioRecordResponse;

        @BeforeEach
        void setUp() {
                sedeRecordResponse = SedeSetUp.sedeRecordResponse();
                Domicilio domicilio = DomicilioSetUp.createDomicilio();
                Sede sede = SedeSetUp.createSede();
                sedeRecord = SedeSetUp.sedeRecord();
                sedeDomiciliosRecord = SedeSetUp.createSedeDomiciliosRecord(sede, domicilio);
                sedeDomicilioRecordResponse = SedeSetUp.createSedeDomicilioRecordResponse();
        }

        @Test
        void getAllByNameAndActive_success() throws Exception {
                given(mockSedeService.getAll(anyString(), anyString(), anyString(), anyString(), any(Estado.class),
                                any(Pageable.class)))
                                .willReturn(new PageImpl<>(Collections.singletonList(sedeDomicilioRecordResponse)));

                mockMvc.perform(
                                get("/api/core/sedes")
                                                .param("nombre", "S")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getById_success() throws Exception {
                given(mockSedeService.findById(anyInt()))
                                .willReturn(sedeRecord);

                mockMvc.perform(
                                get("/api/core/sedes/1")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getById_not_found() throws Exception {
                given(mockSedeService.findById(anyInt()))
                                .willThrow(NotFoundException.class);

                mockMvc.perform(
                                get("/api/core/sedes/0")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void getById_invalid() throws Exception {
                given(mockSedeService.findById(anyInt()))
                                .willThrow(MethodArgumentTypeMismatchException.class);

                mockMvc.perform(
                                get("/api/core/sedes/X")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void create_success() throws Exception {
                String sede = """
                                    {
                                        "id": "1",
                                        "version": "0",
                                        "nombre": "Sede Ejemplo",
                                        "latitude": "19.233503026844463",
                                        "longitude": "-98.23867360110482"
                                    }
                                """;
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "mifotografia.jpg",
                                "image/jpg",
                                "Contenido del archivo".getBytes());
                MockMultipartFile sedeSave = new MockMultipartFile(
                                "sede",
                                "sede",
                                "application/json",
                                sede.getBytes());

                given(mockSedeService.create(SedeSetUp.createSede(Estado.ACTIVE), file))
                                .willReturn(sedeRecordResponse);

                mockMvc.perform(
                                multipart("/api/core/sedes")
                                                .file(file)
                                                .file(sedeSave)
                                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void update_success() throws Exception {
                String sede = """
                                    {
                                        "id": "1",
                                        "version": "0",
                                        "nombre": "Sede Ejemplo",
                                        "latitude": "19.233503026844463",
                                        "longitude": "-98.23867360110482"
                                    }
                                """;
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "mifotografia.jpg",
                                "image/jpg",
                                "Contenido del archivo".getBytes());
                MockMultipartFile sedeSave = new MockMultipartFile(
                                "sede",
                                "sede",
                                "application/json",
                                sede.getBytes());

                given(mockSedeService.create(SedeSetUp.createSede(Estado.ACTIVE), file))
                                .willReturn(sedeRecordResponse);

                mockMvc.perform(
                                multipart("/api/core/sedes")
                                                .file(file)
                                                .file(sedeSave)
                                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void update_error() throws Exception {
                String sede = """
                                    {
                                        "id": "1",
                                        "version": "0",
                                        "nombre": "Sede Ejemplo",
                                        "latitude": "19.233503026844463",
                                        "longitude": "-98.23867360110482"
                                    }
                                """;
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "mifotografia.jpg",
                                "image/jpg",
                                "Contenido del archivo".getBytes());
                MockMultipartFile sedeSave = new MockMultipartFile(
                                "sede",
                                "sede",
                                "application/json",
                                sede.getBytes());

                given(mockSedeService.update(SedeSetUp.createSede(Estado.ACTIVE), file))
                                .willThrow(InvalidVersionException.class);

                mockMvc.perform(
                                multipart("/api/core/sedes")
                                                .file(sedeSave)
                                                .file(file)
                                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void delete_success() throws Exception {
                mockMvc.perform(
                                delete("/api/core/sedes/1")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getAllSedesDomicilios_success() throws Exception {
                given(mockSedeService.getAllSedesAndDomicilios(any(Pageable.class)))
                                .willReturn(new PageImpl<SedeDomiciliosRecord>(
                                                Collections.singletonList(sedeDomiciliosRecord)));

                mockMvc.perform(
                                get("/api/core/sedes/domicilios")
                                                .param("nombre", "S")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }
}
