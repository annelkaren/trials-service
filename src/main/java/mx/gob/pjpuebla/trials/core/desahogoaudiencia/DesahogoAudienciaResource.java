package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/desahogoaudiencia")
@SecurityRequirement(name = "Keycloak")
public class DesahogoAudienciaResource {

    private final DesahogoAudienciaService desahogoAudienciaService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DesahogoAudienciaRecord> getAll(){
        return desahogoAudienciaService.getAll();
    }

}
