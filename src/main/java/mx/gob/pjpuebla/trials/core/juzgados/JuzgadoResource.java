package mx.gob.pjpuebla.trials.core.juzgados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaJuzgadoRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/juzgados")
@SecurityRequirement(name = "Keycloak")
public class JuzgadoResource {

    private final JuzgadoService juzgadoService;
    private final JuzgadoUpdateValidator juzgadoUpdateValidator;

    @GetMapping
    public Page<JuzgadoRecordItem> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return this.juzgadoService.getAll(key, pageable);
    }

    @GetMapping("/oficialias/{id}")
    public List<OficialiaJuzgadoRecord> getByOficialiaId(@PathVariable Integer id) {
        return this.juzgadoService.findByOficialiaId(id);
    }

    @GetMapping("/{id}")
    public JuzgadoRecord getById(@PathVariable Integer id) {
        return this.juzgadoService.findById(id);
    }

    @PostMapping
    public JuzgadoRecordItem create(@RequestBody @Valid Juzgado juzgado) {
        return this.juzgadoService.create(juzgado);
    }

    @PutMapping
    public JuzgadoRecordItem update(@RequestBody @Valid Juzgado juzgado) throws BindException {

        validate(juzgado, juzgadoUpdateValidator);
        return this.juzgadoService.update(juzgado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.juzgadoService.delete(id);
    }

    public static void validate(Object obj, Validator... validators) throws BindException {
        DataBinder dataBinder = new DataBinder(obj);
        dataBinder.addValidators(validators);
        dataBinder.validate();
        dataBinder.close();
    }

    @GetMapping("/autocomplete")
    public List<JuzgadoRecordItem> findAllByEstadoActiveAutocomplete(
            @RequestParam(value = "key", required = false) String key) {
        return this.juzgadoService.findAllByEstadoAutocomplete(key);
    }

    @PatchMapping("/{id}/status/{status}")
    public JuzgadoRecordItem updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.juzgadoService.updateStatus(id, status);
    }
}
