package mx.gob.pjpuebla.trials.core.distritos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/distritos")
@SecurityRequirement(name = "Keycloak")
public class DistritoResource {

    private final DistritoService distritoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<DistritoRecord> getAll(@PageableDefault(size = 25) Pageable pageable) {
        return distritoService.getAllActive(pageable);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DistritoRecord> getAll() {
        return distritoService.getAllActive();
    }

    @GetMapping("/select")
    public List<DistritoRecord> getDistritos() {
        return distritoService.getDistritos();
    }
}
