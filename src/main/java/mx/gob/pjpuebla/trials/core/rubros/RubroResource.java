package mx.gob.pjpuebla.trials.core.rubros;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/rubro")
@SecurityRequirement(name = "keycloak")
public class RubroResource {

    private final RubroService rubroService;

    @GetMapping("/{procedimientoId}")
    public List<RubroRecord> getAllByProcedimiento(@PathVariable Integer procedimientoId) {
        return rubroService.getAllByProcedimiento(procedimientoId);
    }

}
