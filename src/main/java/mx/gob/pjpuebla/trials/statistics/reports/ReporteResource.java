package mx.gob.pjpuebla.trials.statistics.reports;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reportes")
@SecurityRequirement(name = "Keycloak")
public class ReporteResource {

    private final ReporteService reporteService;
    private static final Logger logger = LoggerFactory.getLogger(ReporteResource.class);

    @GetMapping
    public List<ReporteRecord> getAll() {
        return reporteService.getAll();
    }

    @GetMapping(value = "/export",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam(value = "key") String key,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            byte[] bytes = reporteService.generateReport(key, startDate, endDate);
            if (bytes == null || bytes.length == 0) {
                return ResponseEntity.noContent().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(startDate.toString().replace("-", "") + "-"
                                            + endDate.toString().replace("-", "") + "_" + key + ".xlsx",
                                    java.nio.charset.StandardCharsets.UTF_8)
                            .build()
            );

            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            headers.setContentLength(bytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
        } catch (Exception e) {
            logger.error("Error exportando reporte {}: {}", key, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
