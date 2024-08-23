package mx.gob.pjpuebla.trials.core.tipooficialias;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/core/tipooficialia")
@SecurityRequirement(name = "Keycloak")
public class TipoOficialiaResource {

    private final TipoOficialiaService tipoOficialiaService;

    @GetMapping
    public Response getAll(@PageableDefault( size = 20) Pageable pageable){
        return this.tipoOficialiaService.getAll(pageable);
    }
}
