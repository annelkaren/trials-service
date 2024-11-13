package mx.gob.pjpuebla.trials.workflow.identificacion;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/identificacion")
@SecurityRequirement(name = "Keycloak")
public class IdentificacionResource { 

    private final IdentificacionService identificacionService;

    @GetMapping
    public List<IdentificacionDocRecord> getAll(){return this.identificacionService.getAll();}
}
