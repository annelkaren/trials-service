package mx.gob.pjpuebla.trials.workflow.contadoresJuzgados;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/contadoresJuzgados")
@SecurityRequirement(name = "keycloak")
public class ContadorJuzgadoResource {

    private final ContadorJuzgadoService contadorJuzgadoService;

    @GetMapping("")
    public Page<ContadorJuzgadoResponseRecord> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return contadorJuzgadoService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ContadorJuzgadoResponseRecord getById(@PathVariable Integer id) {
        return contadorJuzgadoService.getById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ContadorJuzgadoResponseRecord create(@RequestBody ContadorJuzgadoSaveRecord record) {
        return contadorJuzgadoService.create(record);
    }

    @PutMapping("/{id}")
    public ContadorJuzgadoResponseRecord update(@PathVariable Integer id, @RequestBody ContadorJuzgadoSaveRecord record) {
        return contadorJuzgadoService.update(id, record);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        contadorJuzgadoService.delete(id);
    }
}
