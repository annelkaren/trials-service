package mx.gob.pjpuebla.trials.core.sello;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;


@RestController
@RequestMapping("/api/workflow/documento")
@SecurityRequirement(name = "Keycloak")
public class SelloResource {

    private final SelloService selloService;

    public SelloResource(SelloService selloService) {
        this.selloService = selloService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, FileNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("selloReport", "selloReport.pdf");
        return ResponseEntity.ok().headers(headers).body(selloService.exportPdf(id));
    }
}
