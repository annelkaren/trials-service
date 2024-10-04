package mx.gob.pjpuebla.trials.workflow.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.sello.SelloCaratulaService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.io.IOException;

import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final SelloCaratulaService caratulaGenerator;
    private final DocumentoService documentoService;
    private final DigitalizacionService digitalizacionService;

    @PostMapping("/demanda")
    public DocumentoRecord createDemanda(@RequestBody DocumentoSaveRecord documentoSaveRecord) {
        return this.documentoService.createDemanda(documentoSaveRecord);
    }

    @PatchMapping(value = "/demanda/{id}/anexos", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentoRecord editAnexos(@PathVariable Integer id, @RequestBody AnexoRecord anexoRecord) {
        return documentoService.editarAnexos(id, anexoRecord.anexos(), anexoRecord.motivoEdita());
    }

    @GetMapping(value = "/demanda/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentoResponseRecord> getDemandaById(@PathVariable Integer id) {
        DocumentoResponseRecord editDocumento = documentoService.getDemandaById(id);
        return ResponseEntity.ok(editDocumento);
    }

    @GetMapping(value = "/documentos/{id}/sello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
        return ResponseEntity.ok().headers(headers).body(selloGenerator.exportToPdf(id));
    }

    @PostMapping("/documentos/digitalizacion/{documentoId}")
    public DigitalizacionRecord digitizationDocument(
            @RequestParam("file") MultipartFile file,
            @PathVariable("documentoId") Integer documentoId) {
        return digitalizacionService.procesarArchivo(file, documentoId);
    }

    @GetMapping(value = "/documentos/digitalizacion/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer documentoId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", documentoId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(digitalizacionService.getDocumento(documentoId));
    }


    @GetMapping(value = "/documentos/{id}/caratula", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportCaratulaPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("caratula", id + "_caratula.pdf");
        return ResponseEntity.ok().headers(headers).body(caratulaGenerator.exportToPdf(id));
    }

    @GetMapping("/bandeja/entrada")
    public Page<DocumentoGridRecord> getAll(@PageableDefault(size = 20)  Pageable pageable, @RequestParam(value = "key", required = false) String key) {
        return this.documentoService.getAll(key, pageable);
    }

    @PatchMapping("/bandeja/{id}/status/{status}")
    public DocumentoRecord updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.documentoService.updateStatus(id, status);
    }

    @PostMapping("/apelacion")
    public DocumentoRecord create(@RequestBody @Valid ApelacionRecord apelacionRecord) {
        return this.documentoService.createApelacion(apelacionRecord);
    }
}
