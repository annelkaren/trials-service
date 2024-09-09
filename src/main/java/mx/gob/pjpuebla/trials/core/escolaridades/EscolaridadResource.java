package mx.gob.pjpuebla.trials.core.escolaridades;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/escolaridades")
@SecurityRequirement(name = "Keycloak")
public class EscolaridadResource {
    private final EscolaridadService escolaridadService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EscolaridadRecord> getAll(@PageableDefault(size = 20) Pageable pageable, @RequestParam(value = "nombre", required = false) String nombre) {
        return escolaridadService.getAllActive(pageable, new Escolaridad().setNombre(nombre));
    }
}
