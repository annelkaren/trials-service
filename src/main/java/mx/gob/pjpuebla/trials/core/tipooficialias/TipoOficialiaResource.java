package mx.gob.pjpuebla.trials.core.tipooficialias;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tipooficialia")
@SecurityRequirement(name = "Keycloak")
public class TipoOficialiaResource {

    private final TipoOficialiaService tipoOficialiaService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TipoOficialiaRecord> getAll(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "tipoOficialiaNombre", required = false) String tipoOficialiaNombre
    ){
        TipoOficialia example = new TipoOficialia().setNombre(tipoOficialiaNombre);
        return tipoOficialiaService.getAll(pageable, example);
    }

}
