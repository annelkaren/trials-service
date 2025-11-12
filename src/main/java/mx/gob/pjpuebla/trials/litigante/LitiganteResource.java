package mx.gob.pjpuebla.trials.litigante;

import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.litigante.responselitigante.*;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionesLitiganteRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoPromocionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.gob.pjpuebla.trials.workflow.sello.AcuerdoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/litigante")
@SecurityRequirement(name = "Keycloak")
public class LitiganteResource {

    private final LitiganteService litiganteService;
    private final AcuerdoService acuerdoServicePdf;

    @GetMapping("/expedientes")
    public Page<LitiganteExpedientesRecord> getExpedientesRelacionados(
            @RequestParam String key,
            Pageable pageable) {
        key = (key != null) ? key : "";
        return this.litiganteService.getExpedientesRelacionados(key, pageable);
    }

    @GetMapping("/expedientes/autocomplete")
    public List<LitiganteExpedientesRecord> getAllExpedientesRelacionados() {
        return this.litiganteService.getAllExpedientesRelacionados();
    }

    @GetMapping(value = "/acuerdoSentencia", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AcuerdoSentenciaRecord> getAcuerdosSentencias(Pageable pageable) {
        return litiganteService.getAcuerdosSentencias(pageable);
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

    @GetMapping("/acuerdos/{carpetaId}")
    public Page<DocumentoResponseRecord> getExpedienteDetails(Pageable pageable, @PathVariable Integer carpetaId) {
        return litiganteService.getExpedienteDetails(carpetaId, pageable);
    }

    @GetMapping("/acuerdos/legacy/{cu}")
    public Page<DocumentoResponseRecord> getExpedienteDetailsLegacy(Pageable pageable, @PathVariable String cu) {
        return litiganteService.getExpedienteDetailsLegacy(cu, pageable);
    }

    

    @GetMapping(value = "/promociones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PromocionesLitiganteRecord> getPromocionesLitigante(@RequestParam String key, Pageable pageable) {
        key = (key != null) ? key : "";
        return litiganteService.getPromocionesLitigante(key, pageable);
    }

    @GetMapping(value = "/promociones/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentoPromocionRecord getPromocionById(@PathVariable Integer id) {
        return litiganteService.getPromocionById(id);
    }

    @GetMapping(value = "/librogobierno", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<LibroGobiernoRecord> getConsultaLibroGobierno(
            Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "aPaterno", required = false) String aPaterno,
            @RequestParam(value = "aMaterno", required = false) String aMaterno) {
        return litiganteService.getConsultaLibroGobierno(nombre, aPaterno, aMaterno, pageable);
    }

    @GetMapping(value = "/sentencias/{materiaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SentenciasPublicasRecord> getSentenciasPublicas(
            Pageable pageable,
            @PathVariable Integer materiaId) {
        return litiganteService.getSentenciasPublicas(materiaId, pageable);
    }

    @GetMapping(value = "/expedientes/{materiaId}/{distritoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LitiganteExpedientesRecord> getExpediente(
            @PathVariable Integer materiaId,
            @PathVariable Integer distritoId,
            @RequestParam(value = "expediente", required = false) String expediente,
            @RequestParam(value = "anio", required = false) String anio) {
        return litiganteService.getExpedientes(materiaId, expediente, anio, distritoId);
    }

    @GetMapping(value = "/expediente/{carpetaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExhortoRecord getExpedienteById(
            @PathVariable Integer carpetaId) {
        return litiganteService.getExpedienteById(carpetaId);
    }

    @GetMapping("/pieza/{carpetaId}")
    public List<HistorialRecord> getHistorialByPieza(@PathVariable("carpetaId") Integer carpetaId){
        return litiganteService.getHistorialByPieza(carpetaId);
    }
}
