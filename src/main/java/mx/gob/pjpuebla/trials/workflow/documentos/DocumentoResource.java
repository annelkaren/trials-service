package mx.gob.pjpuebla.trials.workflow.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/documentos")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final DocumentoService documentoService;

    @PostMapping
    public DocumentoRecord createDemanda(@RequestBody DocumentoDTO documentoDTO) {
        return this.documentoService.createDemanda(documentoDTO);
    }

    @GetMapping(value = "/{id}/sello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
        return ResponseEntity.ok().headers(headers).body(selloGenerator.exportToPdf(id));
    }
}
