package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/salas")
@SecurityRequirement(name = "keycloak")
public class SalaResource {

    private final SalaService salaService;

    @GetMapping
    public Page<SalaRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {

        return this.salaService.getAll(new Sala().setNombre(nombre), pageable);
    }

    @GetMapping("/allByJuzgado")
    public List<SalaRecord> getAllByJuzgado(){
        return salaService.getAllByJuzgado();
    }

    @GetMapping("/{id}")
    public SalaRecordResponse getById(@PathVariable Integer id) {
        return this.salaService.findById(id);
    }

    @PostMapping
    public Integer create(@RequestBody @Valid Sala sala) {
        return this.salaService.create(sala);
    }

    @PutMapping
    public Integer update(@RequestBody Sala sala) {
        return this.salaService.update(sala);
    }

    @GetMapping("/juzgado/{idAudiencia}")
    public List<SalaRecord> getAllbyJuzgado(
            @RequestParam(value = "nombre", required = false) String nombre,
            @PathVariable Integer idAudiencia) {
        return this.salaService.getAllbyJuzgado(nombre, idAudiencia);
    }



}
