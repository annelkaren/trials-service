package mx.gob.pjpuebla.trials.workflow.documentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
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
    private CaratulaGenerator caratulaGenerator;

    @MockBean
    private DigitalizacionService digitalizacionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_demanda() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1));
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(1, demanda.getCarpeta().getFolio(), TipoDocumento.DEMANDA);

        given(documentoService.createDemanda(any(DocumentoSaveRecord.class)))
                .willReturn(documentoRecord);

        mockMvc.perform(
                post("/api/workflow/demanda")
                        .content(ResourceUtilTest.asJsonString(documentoRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1));
        demanda.getCarpeta().setFolio("1");
        DocumentoGridRecord documentoGridRecord = new DocumentoGridRecord(1, demanda.getCarpeta().getFolio(), demanda.getCarpeta().getExpediente(),
                "Laboral", TipoDocumento.DEMANDA.name(), LocalDateTime.now(), SelloEstatus.VALIDO, true );

        given(documentoService.getAll(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoGridRecord)));

        mockMvc.perform(
                get("/api/workflow/bandeja/entrada")
                        .content(ResourceUtilTest.asJsonString(documentoGridRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_status() throws Exception {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, new TipoJuicio().setId(1));
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(), TipoDocumento.DEMANDA);

        given(documentoService.updateStatus(demanda.getId(), 1))
                .willReturn(documentoRecord);

        mockMvc.perform(
                patch("/api/workflow/bandeja/1/status/1")
                        .content(ResourceUtilTest.asJsonString(documentoRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }


//    @Test
//    void edit_anexos() throws  Exception{
//
//        AnexoRecord anexoRecord = new AnexoRecord(
//                Arrays.asList("Anexo1", "Anexo2"),
//                "Motivo de edición"
//        );
//
//
//        AnexoRecord updatedAnexos = new AnexoRecord(
//                Arrays.asList("Anexo1 actualizado", "Anexo2 actualizado"),
//                "Motivo de edición actualizado"
//        );
//
//        given(documentoService.editarAnexos(any(Integer.class), any(), any()))
//                .willReturn(updatedAnexos);
//
//        mockMvc.perform(
//                patch("/api/workflow/demanda/1/anexos")
//                        .content(asJsonString(anexoRecord))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isOk());
//    }


//    @Test
//    void getEditDocumento() throws Exception {
//
//    PersonaDocumentoDTO actor = new PersonaDocumentoDTO();
//    actor.setNombre("John");
//    actor.setApellidoPaterno("Doe");
//    actor.setApellidoMaterno("Smith");
//    actor.setPseudonimo("JD");
//    actor.setTipoPersona("fisica");
//    actor.setTipoParte(1);
//
//    PersonaDocumentoDTO demandado = new PersonaDocumentoDTO();
//    demandado.setNombre("Jane");
//    demandado.setApellidoPaterno("Doe");
//    demandado.setApellidoMaterno("Johnson");
//    demandado.setPseudonimo("JJ");
//    demandado.setTipoPersona("fisica");
//    demandado.setTipoParte(2);
//    List<String> anexos = List.of("Anexo1", "Anexo2");
//
//
//    DocumentoResponseRecord documentoResponse = new DocumentoResponseRecord(actor, demandado, anexos);
//
//
//    given(documentoService.getEditDocumentoAnexo(any(Integer.class)))
//            .willReturn(documentoResponse);
//
//
//    mockMvc.perform(
//                    get("/api/workflow/demanda/1")
//                            .accept(MediaType.APPLICATION_JSON)
//            ).andExpect(status().isOk());
//
//}
}
