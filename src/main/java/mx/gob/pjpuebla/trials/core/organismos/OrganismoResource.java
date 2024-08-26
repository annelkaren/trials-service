package mx.gob.pjpuebla.trials.core.organismos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.estadoCivil.EstadoCivil;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/core/organismos")
@SecurityRequirement(name = "Keycloak")
public class OrganismoResource {
    private final OrganismoService organismoService;

    @GetMapping
    public List<Organismo> getAll(@PageableDefault Pageable pageable){
        return this.organismoService.getAll(pageable);
    }

}
