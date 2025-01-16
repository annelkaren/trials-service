package mx.gob.pjpuebla.trials.litigante;

import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionAutorizadaRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import mx.gob.pjpuebla.trials.litigante.responselitigante.ExpedienteAutorizadoRecord;
import mx.gob.pjpuebla.trials.workflow.sello.AcuerdoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/litigante")
@SecurityRequirement(name = "Keycloak")
public class LitiganteResource {

    private final LitiganteService litiganteService;
    private final AcuerdoService acuerdoServicePdf;

    @GetMapping("/expedientes")
    public Page<LitiganteExpedientesRecord> getExpedientesRelacionados(Pageable pageable) {
        return this.litiganteService.getExpedientesRelacionados(pageable);
    }

    @GetMapping(value = "/acuerdoSentencia", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExpedienteAutorizadoRecord getAcuerdosSentencias(){
        return litiganteService.getAcuerdosSentencias();
    }

    @GetMapping(value = "/documento/{documentoId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportAcuerdoPdf(@PathVariable Integer documentoId) throws JRException, IOException, WriterException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("acuerdo", documentoId + "_Documento.pdf");
        return ResponseEntity.ok().headers(headers).body(acuerdoServicePdf.getAcuerdoPdf(documentoId));
    }

    @GetMapping("/audiencias")
    public Page<LitiganteExpedienteListAudienciasRecord> getAudienciasByExpedienteRelacionados(Pageable pageable) {
        return this.litiganteService.getExpedientesAudienciasRelacionados(pageable);
    }

    @GetMapping(value = "/promociones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PromocionAutorizadaRecord> getPromocionesLitigante(Pageable pageable) {
        return litiganteService.getPromocionesLitigante(pageable);
    }
}
