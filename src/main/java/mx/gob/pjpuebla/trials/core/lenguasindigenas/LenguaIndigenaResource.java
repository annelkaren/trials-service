package mx.gob.pjpuebla.trials.core.lenguasindigenas;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/lenguasindigenas")
@SecurityRequirement(name = "Keycloak")
public class LenguaIndigenaResource {

    private final LenguaIndigenaService lenguaIndigenaService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LenguaIndigenaRecord> getAll(@RequestParam(value = "key", required = false) String key) {
        return lenguaIndigenaService.getAll(key);
    }
}
