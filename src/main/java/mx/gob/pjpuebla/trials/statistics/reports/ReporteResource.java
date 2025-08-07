package mx.gob.pjpuebla.trials.statistics.reports;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
