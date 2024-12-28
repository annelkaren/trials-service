package mx.gob.pjpuebla.trials.core.cuestionarios;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/cuestionario")
@SecurityRequirement(name = "keycloak")
public class CuestionarioResource {

    private final CuestionarioService cuestionarioService;

    @GetMapping("/{lista}")
    public List<CuestionarioRecord> findByLista(@PathVariable Integer lista) {
        return this.cuestionarioService.getListByLista(lista);
    }
}
