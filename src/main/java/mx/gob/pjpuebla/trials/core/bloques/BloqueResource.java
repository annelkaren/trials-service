package mx.gob.pjpuebla.trials.core.bloques;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;

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

    @PostMapping
    public BloqueRecordResponse create(@RequestBody @Valid Bloque bloque) {
        return this.bloqueService.create(bloque);
    }

    @PutMapping
    public BloqueRecordResponse update(@RequestBody @Valid Bloque bloque) {
        return this.bloqueService.update(bloque);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.bloqueService.delete(id);
    }

}
