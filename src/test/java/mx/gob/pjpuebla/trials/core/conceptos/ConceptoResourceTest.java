package mx.gob.pjpuebla.trials.core.conceptos;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.error.NotFoundException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConceptoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ConceptoResourceTest {

    @MockBean
    private ConceptoService mockConceptoService;

    @Autowired
    private MockMvc mockMvc;

    private ConceptoRecordResponse conceptoRecordResponse;

    @BeforeEach
    void setUp() {
        conceptoRecordResponse = ConceptoSetUp.createConceptoRecordResponse();
    }

    @Test
    void getAll_success() throws Exception {
        List<ConceptoRecordResponse> conceptosList = List.of(conceptoRecordResponse);

        given(mockConceptoService.getAll(1))
                .willReturn(conceptosList);

        mockMvc.perform(
                get("/api/core/conceptos/carpetaId/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockConceptoService.findById(anyInt()))
                .willReturn(conceptoRecordResponse);

        mockMvc.perform(
                get("/api/core/conceptos/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockConceptoService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/conceptos/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockConceptoService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/conceptos/X")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getAllConceptos_success() throws Exception {

        ConceptoRecord conceptoRecord = new ConceptoRecord(1, "Distribución", 3, "Familiar (Tradicional)", Estado.ACTIVE);
        List<ConceptoRecord> conceptosList = List.of(conceptoRecord);

        given(mockConceptoService.getAllConceptos(any(Pageable.class), anyString()))
                .willReturn(new PageImpl<>(conceptosList));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/core/conceptos/registros")
                        .param("nombre", "nombre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_success() throws Exception {
        ConceptoRecord conceptoRecord = new ConceptoRecord(1, "Adjuntar", 1, "Tipo Juicio", Estado.ACTIVE);

        given(mockConceptoService.updateStatus(anyInt(), anyInt())).willReturn(conceptoRecord);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/core/conceptos/1/status/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/core/conceptos/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create_success() throws Exception {
        ConceptoRecord conceptoRecord = new ConceptoRecord(1, "Nuevo concepto", 1, "Tipo Juicio", Estado.ACTIVE);

        given(mockConceptoService.createConcepto(any(ConceptoBulkRequest.class))).willReturn(List.of(conceptoRecord));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/core/conceptos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Nuevo concepto\",\"dias\":1,\"estado\":\"ACTIVE\",\"tipoJuicios\":[{\"id\":1}]}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdConceptoJuicio_success() throws Exception {
        ConceptoRecordJuicio conceptoRecordJuicio = new ConceptoRecordJuicio(1, "Adjuntar", "Tipo Juicio", 1, Estado.ACTIVE, 1, 1);

        given(mockConceptoService.findByConceptoById(anyInt())).willReturn(conceptoRecordJuicio);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/core/conceptos/conceptoJuicio/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        ConceptoRecord conceptoRecord = new ConceptoRecord(1, "Concepto actualizado", 10, "Tipo Juicio", Estado.INACTIVE);

        given(mockConceptoService.updateConcepto(any(ConceptoBulkRequest.class))).willReturn(conceptoRecord);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/core/conceptos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"nombre\":\"Concepto actualizado\",\"dias\":10,\"estado\":\"INACTIVE\",\"tipoJuicios\":[{\"id\":1}]}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
