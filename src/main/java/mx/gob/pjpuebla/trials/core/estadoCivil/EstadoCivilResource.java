package mx.gob.pjpuebla.trials.core.estadoCivil;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/estadocivil")
@SecurityRequirement(name = "Keycloak")
public class EstadoCivilResource {
    private final EstadoCivilService estadoCivilService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EstadoCivilRecord> getAll(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "materiaNombre", required = false) String EstadoCivilNombre)
    {
        EstadoCivil example = new EstadoCivil().setNombre(EstadoCivilNombre);
        return estadoCivilService.getAll(pageable, example);
    }
}
