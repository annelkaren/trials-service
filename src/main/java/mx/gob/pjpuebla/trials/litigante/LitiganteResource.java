package mx.gob.pjpuebla.trials.litigante;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/litigante")
@SecurityRequirement(name = "Keycloak")
public class LitiganteResource {

    private final LitiganteService litiganteService;

    @GetMapping("/expedientes")
    public Page<LitiganteExpedientesRecord> getExpedientesRelacionados(Pageable pageable) {
        return this.litiganteService.getExpedientesRelacionados(pageable);
    }
}
