package mx.gob.pjpuebla.trials.core.procedimientos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/procedimiento")
@SecurityRequirement(name = "keycloak")
public class ProcedimientoResource {

    private final ProcedimientoService procedimientoService;

    @GetMapping("/{tipoJuicioId}")
    public List<ProcedimientoRecord> getAllByTipoJuicio(@PathVariable Integer tipoJuicioId) {
        return procedimientoService.getAllByTipoJuicio(tipoJuicioId);
    }

}
