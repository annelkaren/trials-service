package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.sello.SelloCaratulaService;
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
import java.util.*;

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
    private SelloCaratulaService caratulaGenerator;

    @MockBean
    private DigitalizacionService digitalizacionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_demanda() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(1, demanda.getCarpeta().getFolio(),
                TipoCarpeta.DEMANDA);

        given(documentoService.createDemanda(any(DocumentoSaveRecord.class)))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        post("/api/workflow/demanda")
                                .content(ResourceUtilTest.asJsonString(documentoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        DocumentoGridRecord documentoGridRecord = new DocumentoGridRecord(1, demanda.getCarpeta().getFolio(),
                demanda.getCarpeta().getExpediente(),
                "Laboral", TipoCarpeta.DEMANDA.name(), LocalDateTime.now(), SelloEstatus.VALIDO,
                true);

        given(documentoService.getAll(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoGridRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/entrada")
                                .content(ResourceUtilTest.asJsonString(documentoGridRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_status() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(),
                TipoCarpeta.DEMANDA);

        given(documentoService.updateStatus(demanda.getId(), 1))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        patch("/api/workflow/bandeja/1/status/1")
                                .content(ResourceUtilTest.asJsonString(documentoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void edit_anexos() throws Exception {

        AnexoRecord anexoRecord = new AnexoRecord(
                Arrays.asList("Anexo1", "Anexo2"),
                "Motivo de edición");

        DocumentoRecord documentoRecord = new DocumentoRecord(1, "000001/2024", TipoCarpeta.DEMANDA);

        given(documentoService.editarAnexos(any(Integer.class), any(), any()))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        patch("/api/workflow/demanda/1/anexos")
                                .content(ResourceUtilTest.asJsonString(anexoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEditDocumento() throws Exception {

        List<String> anexos = List.of("Anexo1", "Anexo2");

        DocumentoRecord documentRecord = new DocumentoRecord(1, "000005/2024", TipoCarpeta.DEMANDA);

        given(documentoService.editarAnexos(1, anexos, "ejemplo"))
                .willReturn(documentRecord);

        mockMvc.perform(
                        get("/api/workflow/demanda/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
