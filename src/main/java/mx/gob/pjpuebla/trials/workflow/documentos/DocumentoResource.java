package mx.gob.pjpuebla.trials.workflow.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final DocumentoService documentoService;
    private final DigitalizacionService digitalizacionService;

    @GetMapping("/bandeja/entrada")
    public Page<DocumentoGridRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return this.documentoService.getAll(key, pageable);
    }

    @PatchMapping("/bandeja/entrada/{id}/status/{status}")
    public DocumentoRecord updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.documentoService.updateStatus(id, status);
    }

    @PostMapping("/demanda")
    public DocumentoRecord createDemanda(@RequestBody DocumentoDTO documentoDTO) {
        return this.documentoService.createDemanda(documentoDTO);
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
        @PathVariable("documentoId") Integer documentoId) throws IOException {
    
        return digitalizacionService.procesarArchivo(file, documentoId);
    }
}
