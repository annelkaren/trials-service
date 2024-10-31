package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;

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


}
