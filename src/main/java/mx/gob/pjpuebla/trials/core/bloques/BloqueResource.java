package mx.gob.pjpuebla.trials.core.bloques;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/bloques")
@SecurityRequirement(name = "keycloak")
public class BloqueResource {

    private final BloqueService bloqueService;

    @GetMapping
    public Page<BloqueRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "horaInicial", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicial) {

        return this.bloqueService.getAll(new Bloque().setHoraInicial(horaInicial), pageable);
    }

}
