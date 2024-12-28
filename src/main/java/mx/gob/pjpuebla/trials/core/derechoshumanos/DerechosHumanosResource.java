package mx.gob.pjpuebla.trials.core.derechoshumanos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/derechosHumanos")
@SecurityRequirement(name = "keycloak")
public class DerechosHumanosResource {

    private final DerechosHumanosService derechosHumanosService;

    @GetMapping("/{tipo}")
    public List<DerechosHumanosRecord> findById(@PathVariable String tipo) {
        return this.derechosHumanosService.getListByTipo(tipo);
    }
}
