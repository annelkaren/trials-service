package mx.gob.pjpuebla.trials.core.tiposistema;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tiposistema")
@SecurityRequirement(name = "Keycloak")
public class TipoSistemaResource {
    private final TipoSistemaService tipoSistemaService;

    @GetMapping
    public List<TipoSistemaRecord> getAll(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "tipoSistemaNombre", required = false) String tipoSistemaNombre
    ){
        TipoSistema example = new TipoSistema().setNombre(tipoSistemaNombre);
        return tipoSistemaService.getAll(pageable, example);
    }

}

