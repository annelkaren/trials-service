package mx.gob.pjpuebla.trials.workflow.documentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.sello.CaratulaGenerator;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DocumentoResourceTest {

    @MockBean
    private DocumentoService documentoService;

    @MockBean
    private SelloGenerator selloGenerator;

    @MockBean
    private CaratulaGenerator caratulaGenerator;

    @MockBean
    private DigitalizacionService digitalizacionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_demanda() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1)).setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(1, demanda.getFolio(), TipoDocumento.DEMANDA);

        given(documentoService.createDemanda(any(DocumentoDTO.class)))
                .willReturn(documentoRecord);

        mockMvc.perform(
                post("/api/workflow/demanda")
                        .content(asJsonString(documentoRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1)).setFolio("1");
        DocumentoGridRecord documentoGridRecord = new DocumentoGridRecord(1, demanda.getFolio(), demanda.getExpediente(),
                "Laboral", TipoDocumento.DEMANDA.name(), LocalDateTime.now(), SelloEstatus.VALIDO, true );

        given(documentoService.getAll(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoGridRecord)));

        mockMvc.perform(
                get("/api/workflow/bandeja/entrada")
                        .content(asJsonString(documentoGridRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_status() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1)).setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getFolio(), TipoDocumento.DEMANDA);

        given(documentoService.updateStatus(demanda.getId(), 1))
                .willReturn(documentoRecord);

        mockMvc.perform(
                patch("/api/workflow/bandeja/1/status/1")
                        .content(asJsonString(documentoRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }


    @Test
    void edit_anexos() throws  Exception{

        AnexoRecord anexoRecord = new AnexoRecord(
                Arrays.asList("Anexo1", "Anexo2"),
                "Motivo de edición"
        );


        AnexoRecord updatedAnexos = new AnexoRecord(
                Arrays.asList("Anexo1 actualizado", "Anexo2 actualizado"),
                "Motivo de edición actualizado"
        );

        given(documentoService.editarAnexos(any(Integer.class), any(), any()))
                .willReturn(updatedAnexos);

        mockMvc.perform(
                patch("/api/workflow/demanda/1/anexos")
                        .content(asJsonString(anexoRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getEditDocumento() throws Exception {
        Map<String, Object> editDocumento = new HashMap<>();
        editDocumento.put("key", "value"); 

        given(documentoService.getEditDocumentoAnexo(any(Integer.class)))
                .willReturn(editDocumento);

        mockMvc.perform(
                get("/api/workflow/demanda/1")
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
