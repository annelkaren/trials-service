package mx.gob.pjpuebla.trials.core.bloques;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/bloques")
@SecurityRequirement(name = "keycloak")
public class BloqueResource {

    private final BloqueService bloqueService;

    @GetMapping
    public Page<BloqueRecordResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "estatus", required = false) Estado estatus) {

        return this.bloqueService.getAll(key, estatus, pageable);
    }

    @GetMapping("/{id}")
    public BloqueRecordResponse getById(@PathVariable Integer id) {
        return this.bloqueService.findById(id);
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
