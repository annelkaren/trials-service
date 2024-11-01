package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "keycloak")
public class DocumentoDetalleResources {
    private final DocumentoDetalleService documentoDetalleService;

    @PostMapping("documentoDetalle/digitalizar/acuse")
    public Integer digitalizarAcuse(@RequestBody DocumentoDetalleRecord documento) {
        return documentoDetalleService.digitalizacionAcuse(documento);
    }

    @GetMapping(value = "documentoDetalle/digitalizar/acuse/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer documentoId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("acuse", documentoId + "_acuse.pdf");
    
        return ResponseEntity.ok().headers(headers).body(documentoDetalleService.getAcuse(documentoId));
    }
    


}
