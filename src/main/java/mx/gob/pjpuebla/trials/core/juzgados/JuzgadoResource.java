package mx.gob.pjpuebla.trials.core.juzgados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/juzgados")
@SecurityRequirement(name = "Keycloak")
public class JuzgadoResource {

    private final JuzgadoService juzgadoService;

    @GetMapping
    public Page<JuzgadoRecordResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.juzgadoService.getAll(new Juzgado().setNombre(nombre), pageable);
    }

    @GetMapping("all")
    public List<JuzgadoRecordResponse> getAllWithoutPagination() {
        return this.juzgadoService.getAllWithoutPagination();
    }

    @GetMapping("/{id}")
    public JuzgadoRecord getById(@PathVariable Integer id) {
        return this.juzgadoService.findById(id);
    }

    @PostMapping
    public JuzgadoRecordResponse create(@RequestBody @Valid Juzgado juzgado) {
        return this.juzgadoService.create(juzgado);
    }

    @PostMapping("/relacionjuicio")
    public RelJuzgadoTipoJuicio createTipoJuicio(@RequestBody @Valid RelJuzgadoTipoJuicio relJuzgadoTipoJuicio) {
        return this.juzgadoService.createRelacion(relJuzgadoTipoJuicio);
    }

    @PutMapping
    public JuzgadoRecordResponse update(@RequestBody @Valid Juzgado juzgado) {
        return this.juzgadoService.update(juzgado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.juzgadoService.delete(id);
    }
}
