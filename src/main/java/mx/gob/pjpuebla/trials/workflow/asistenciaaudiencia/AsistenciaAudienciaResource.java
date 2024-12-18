package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/audienciaasistencia")
@SecurityRequirement(name = "Keycloak")
public class AsistenciaAudienciaResource {

    private final AsistenciaAudienciaService asistenciaAudienciaService;

    @GetMapping
    public Page<AsistenciaAudienciaResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return this.asistenciaAudienciaService.getAll(pageable);
    }
}
