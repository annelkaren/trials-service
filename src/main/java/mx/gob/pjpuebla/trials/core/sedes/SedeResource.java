package mx.gob.pjpuebla.trials.core.sedes;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/sedes")
@SecurityRequirement(name = "Keycloak")
public class SedeResource {

    private final SedeService sedeService;

    @GetMapping
    public Page<SedeDomicilioRecordResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.sedeService.getAll(new Sede().setNombre(nombre), pageable);
    }

    @GetMapping("/{id}")
    public SedeRecord getById(@PathVariable Integer id) {
        return this.sedeService.findById(id);
    }

    @PostMapping
    public SedeRecordResponse create(@RequestBody @Valid Sede sede) {
        return this.sedeService.create(sede);
    }

    @PutMapping
    public SedeRecordResponse update(@RequestBody @Valid Sede sede) {
        return this.sedeService.update(sede);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.sedeService.delete(id);
    }

    @GetMapping("/domicilios")
    public Page<SedeDomiciliosRecord> getAllDomicilosOfSede(@PageableDefault(size = 20) Pageable pageable) {
        return this.sedeService.getAllSedesAndDomicilios(pageable);
    }
}
