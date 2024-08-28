package mx.gob.pjpuebla.trials.core.organismos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/organismos")
@SecurityRequirement(name = "Keycloak")
public class OrganismoResource {
    private final OrganismoService organismoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrganismoRecord> getAll(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "organismoNombre", required = false) String EstadoOrganismos
    ){
        Organismo example = new Organismo().setNombre(EstadoOrganismos);
        return organismoService.getAll(pageable, example);
    }

}
