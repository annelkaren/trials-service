package mx.gob.pjpuebla.trials.core.religiones;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/religiones")
@SecurityRequirement(name = "Keycloak")
public class ReligionesResource {

    private final ReligionesService religionesService;

    @GetMapping("/autocomplete")
    public List<ReligionesRecord> findAllByreligionesAutocomplete(
            @RequestParam(value = "key", required = false) String key) {
        return  religionesService.findAllByReligionesAutocomplete(key);
    }
}
