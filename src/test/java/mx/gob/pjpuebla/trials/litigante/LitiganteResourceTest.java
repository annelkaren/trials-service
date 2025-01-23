package mx.gob.pjpuebla.trials.litigante;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.litigante.responselitigante.AcuerdoSentenciaRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.DocumentoExpedienteRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.ExpedienteAutorizadoRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasExpedienteRecord;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionAutorizadaRecord;
import mx.gob.pjpuebla.trials.workflow.sello.AcuerdoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LitiganteResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class LitiganteResourceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LitiganteService litiganteService;
    @MockBean
    private AcuerdoService acuerdoServicePdf;

    @Test 
    void getExpedientesRelacionados() throws Exception {
        LitiganteExpedientesRecord litiganteExpedientesRecord = new LitiganteExpedientesRecord(
                100, "000001/2025", "MERCANTIL", "Mercantil (Tradicional)",
                "", "", "Juzgado 5 Mercantil TEST", 0L);
        given(litiganteService.getExpedientesRelacionados(any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(litiganteExpedientesRecord)));

        mockMvc.perform(
                get("/api/litigante/expedientes")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAcuerdosSentencias() throws Exception {
        List<DocumentoExpedienteRecord> derl = Collections.singletonList(new DocumentoExpedienteRecord(
                123, "2025-01-11", "12:03:04", "/api/litigante/documento/123"));
        List<AcuerdoSentenciaRecord> asrl = Collections.singletonList(
                new AcuerdoSentenciaRecord("000001/2025", derl));
        ExpedienteAutorizadoRecord expedienteAutorizadoRecord = new ExpedienteAutorizadoRecord(asrl);

        given(litiganteService.getAcuerdosSentencias()).willReturn(expedienteAutorizadoRecord);

        mockMvc.perform(get("/api/litigante/acuerdoSentencia")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void exportAcuerdoPdf() throws Exception {
        Integer documentoId = 123;
        byte[] mockPdf = new byte[]{1, 2, 3};

        given(acuerdoServicePdf.getAcuerdoPdf(documentoId)).willReturn(mockPdf);

        mockMvc.perform(get("/api/litigante/documento/" + documentoId)
                        .accept(APPLICATION_PDF))
                .andExpect(status().isOk());
    }

    @Test
    void getPromocionesLitigante() throws Exception {
        PromocionAutorizadaRecord promocionRecord = new PromocionAutorizadaRecord(Collections.emptyList());
        Page<PromocionAutorizadaRecord> promocionesPage = new PageImpl<>(Collections.singletonList(promocionRecord));

        given(litiganteService.getPromocionesLitigante(any(Pageable.class))).willReturn(promocionesPage);
        mockMvc.perform(get("/api/litigante/promociones")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAudienciasByExpedienteRelacionados() throws Exception {
        LitiganteExpedienteListAudienciasRecord litiganteExpedienteListAudienciasRecord = new LitiganteExpedienteListAudienciasRecord(
                100, "000001/2025", "MERCANTIL", "Mercantil (Tradicional)",
                "Juzgado 5 Mercantil TEST",
                Collections.singletonList(new AudienciasExpedienteRecord(100, "2025-01-13", "08:00:00", "2025-01-13", "08:30:00"))
        );

        given(litiganteService.getExpedientesAudienciasRelacionados(any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(litiganteExpedienteListAudienciasRecord)));

        mockMvc.perform(
                get("/api/litigante/audiencias")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

        void getExpedienteDetails() throws Exception {
                DocumentoResponseRecord documentoResponse = new DocumentoResponseRecord(
                        "001",
                        LocalDate.of(2025, 1, 1),
                        LocalTime.of(10, 0, 0),
                        "/archivos/documento-prueba.pdf"
                );

                ExpedienteResponseRecord expedienteResponse = new ExpedienteResponseRecord(
                        "000001/2025", 
                        "MERCANTIL", 
                        "Juicio Ordinario Mercantil", 
                        "Juzgado 5 Mercantil", 
                        2L, 
                        List.of(documentoResponse)
                );

                given(litiganteService.getExpedienteDetails()).willReturn(expedienteResponse);

                mockMvc.perform(
                        get("/api/litigante/acuerdos")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        }

}
