package mx.gob.pjpuebla.trials.core.tipoJuicio;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tipo-juicio")
@SecurityRequirement(name = "Keycloak")
public class TipoJuicioResource {

    private final TipoJuicioService tipoJuicioService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TipoJuicioRecord> getAll(
                @PageableDefault(size = 20) Pageable pageable,
                @RequestParam(value = "tipoJuicioNombre", required = false) String tipoJuicioNombre
    ) {
        return tipoJuicioService.getAllActive(pageable, new TipoJuicio().setNombre(tipoJuicioNombre));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TipoJuicioRecord getById(@PathVariable Integer id) {
        return tipoJuicioService.findById(id);
    }

}
