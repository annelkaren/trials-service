package mx.gob.pjpuebla.trials.core.paises;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/paises")
@SecurityRequirement(name = "Keycloak")
public class PaisResource {

    private final PaisService paisService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PaisRecord> getAll(){
        return paisService.getAll();
    }

}
