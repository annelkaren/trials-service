package mx.gob.pjpuebla.trials.workflow.documentos.acuerdos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import mx.gob.pjpuebla.trials.workflow.sello.AcuerdoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;

@WebMvcTest(AcuerdosResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AcuerdoResourceTest {

    @MockBean
    private AcuerdosService acuerdosService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AcuerdoService acuerdoServicePdf;

    @Test
    void crear_acuerdo() throws Exception {
        AcuerdoRecord acuerdo = AcuerdoRecordSetUp.create();

        given(acuerdosService.save(acuerdo)).willReturn(new DocumentoGenericRecord(1, TipoDocumento.ACUERDO));

        mockMvc.perform(
                post("/api/workflow/documentos/crearAcuerdo")
                        .content(ResourceUtilTest.asJsonString(acuerdo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtener_acuerdos() throws Exception {

        List<AcuerdosRecord> acuerdoRecord = AcuerdoRecordSetUp.createAcuerdoRecord();

        given(acuerdosService.getAcuerdos(anyInt(), any(Pageable.class)))
                .willReturn(new PageImpl<>(acuerdoRecord));

        mockMvc.perform(
                get("/api/workflow/documentos/obtenerAcuerdos/{carpetaId}", 1)
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Verificar que el estado sea 200 OK

    }

    @Test
    void publicarAcuerdo() throws Exception {
        AcuerdoRecord acuerdo = AcuerdoRecordSetUp.create();

        given(acuerdosService.publicarAcuerdo(acuerdo)).willReturn(acuerdo);

        mockMvc.perform(
                post("/api/workflow/documentos/publicarAcuerdo")
                        .content(ResourceUtilTest.asJsonString(acuerdo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTipoPartesAcuerdo() throws Exception {
        List<AcuerdoNotificadosRecord> acuerdoNotificacion = AcuerdoRecordSetUp.createAcuerdoNotificadoRecord();

        given(acuerdosService.getTipoPartesAcuerdo(1, "actor")).willReturn(acuerdoNotificacion);

        mockMvc.perform(
                get("/api/workflow/documentos/obtenerTipoPartesAcuerdo/{carpetaId}/{tipoParte}", 1, "actor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPromocionesTest() throws Exception {
        List<AcuerdoPromocionesRecord> acuerdoPromocionesRecord = AcuerdoRecordSetUp.createAcuerdoPromocionesRecord();

        given(acuerdosService.obtenerPromociones(anyInt(), anyInt(), anyString()))
                .willReturn(acuerdoPromocionesRecord);

        mockMvc.perform(
                get("/api/workflow/documentos/obtenerPromociones/{carpetaId}/{documentoId}/{tipoDocumento}", 1,1, "ACUERDO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAcuerdo() throws Exception {
        AcuerdoRecord acuerdo = AcuerdoRecordSetUp.create();

        given(acuerdosService.getAcuerdoOSentencia(anyInt())).willReturn(acuerdo);

        mockMvc.perform(
                get("/api/workflow/documentos/obtenerAcuerdo/{acuerdoId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarAcuerdo() throws Exception {
        DocumentoGenericRecord documento = new DocumentoGenericRecord(1, TipoDocumento.ACUERDO);
        AcuerdoRecord acuerdo = AcuerdoRecordSetUp.create();

        given(acuerdosService.update(acuerdo)).willReturn(documento);

        mockMvc.perform(
                put("/api/workflow/documentos/actualizarAcuerdo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResourceUtilTest.asJsonString(acuerdo)))
                .andExpect(status().isOk());
    }

    @Test
    void get_acuerdo_pdf() throws Exception {
        Integer documentoId = 89734;
        byte[] mockPdf = new byte[]{1, 2, 3};

        given(acuerdoServicePdf.getAcuerdoPdf(documentoId)).willReturn(mockPdf);

        mockMvc.perform(get("/api/workflow/documentos/acuerdos/{documentoId}", documentoId)
                        .accept(APPLICATION_PDF))
                .andExpect(status().isOk());
    }


}
