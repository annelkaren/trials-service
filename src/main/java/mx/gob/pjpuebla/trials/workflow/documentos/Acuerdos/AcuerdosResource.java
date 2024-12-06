package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.io.IOException;
import java.util.List;

import com.google.zxing.WriterException;
import mx.gob.pjpuebla.trials.workflow.sello.AcuerdoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class AcuerdosResource {

    private final AcuerdosService acuerdosService;
    private final AcuerdoService acuerdoServicePdf;

    @PostMapping("/documentos/crearAcuerdo")
    public DocumentoGenericRecord crearAcuerdo(@RequestBody AcuerdoRecord acuerdo) {
        
        return acuerdosService.save(acuerdo);
    }

    @GetMapping("/documentos/obtenerAcuerdos/{carpetaId}")
    public Page<AcuerdosRecord> obtenerAcuerdosYsentencias(
        @PageableDefault(size = 20) Pageable pageable,
        @PathVariable Integer carpetaId){

            return acuerdosService.getAcuerdos(carpetaId, pageable);
    }

    @PostMapping("/documentos/publicarAcuerdo")
    public AcuerdoRecord publicarAcuerdo(@RequestBody AcuerdoRecord acuerdo){
        return acuerdosService.publicarAcuerdo(acuerdo);
    }

    @GetMapping("/documentos/obtenerTipoPartesAcuerdo/{carpetaId}/{tipoParte}")
    public List<AcuerdoNotificadosRecord> obtenerTipoPartesAcuerdo(@PathVariable Integer carpetaId, @PathVariable String tipoParte) {
        return acuerdosService.getTipoPartesAcuerdo(carpetaId, tipoParte);
    }

    @GetMapping("/documentos/obtenerPromociones/{carpetaId}/{documentoId}/{tipoDocumento}")
    public List<AcuerdoPromocionesRecord> obtenerPromociones(
            @PathVariable Integer carpetaId,
            @PathVariable(required = false) String documentoId,
            @PathVariable String tipoDocumento){

            Integer documentoIdParsed = "null".equalsIgnoreCase(documentoId) ? null : Integer.valueOf(documentoId);

            return acuerdosService.obtenerPromociones(carpetaId, documentoIdParsed, tipoDocumento);
    }

    @GetMapping("/documentos/obtenerAcuerdo/{acuerdoId}")
    public Object obtenerAcuerdoOSentencias(@PathVariable Integer acuerdoId) {
        return acuerdosService.getAcuerdoOSentencia(acuerdoId);
    }

    @PutMapping("/documentos/actualizarAcuerdo")
    public DocumentoGenericRecord actualizarAcuerdo(@RequestBody AcuerdoRecord acuerdo) {

        return acuerdosService.update(acuerdo);
    }

    @GetMapping(value = "/acuerdos/{documentoId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportAcuerdoPdf(@PathVariable Integer documentoId) throws JRException, IOException, WriterException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("acuerdo", documentoId + "_Documento.pdf");
        return ResponseEntity.ok().headers(headers).body(acuerdoServicePdf.getAcuerdoPdf(documentoId));
    }

    @GetMapping(value = "/documentos/acuerdos/{acuerdoId}/promociones")
    public List<AcuerdoPromocionesRecord> getPromocionesAcuerdos(@PathVariable Integer acuerdoId){
        return acuerdosService.findPromocionesByAcuerdo(acuerdoId);
    }

}
