package mx.gob.pjpuebla.trials.workflow.cargaTrabajo;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "keycloak")
public class CargaTrabajoResource {

    private final CargaTrabajoService cargaTrabajoService;

    @GetMapping(value = "/documentos/reporteCarga", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportReporteCarga() throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("carga_de_trabajo", ".pdf");
        return ResponseEntity.ok().headers(headers).body(cargaTrabajoService.exportToPdf());
    }
}
