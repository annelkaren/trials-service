package mx.gob.pjpuebla.trials.statistics.reports;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reportes")
@SecurityRequirement(name = "Keycloak")
public class ReporteResource {

    private final ReporteService reporteService;

    @GetMapping
    public List<ReporteDatesRecord> getDatesByMateria() {
        return reporteService.getDatesByMateria();
    }

    @GetMapping(value = "/export")
    public ResponseEntity<byte[]> getFile() throws Exception {
        byte[] bytes = reporteService.exportarBytes();

        String filename = "DIVORCIOS.xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(bytes.length);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename, java.nio.charset.StandardCharsets.UTF_8)
                        .build()
        );

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
