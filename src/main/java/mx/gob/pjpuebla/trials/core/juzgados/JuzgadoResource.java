package mx.gob.pjpuebla.trials.core.juzgados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Collections;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaJuzgadoRecord;
import mx.gob.pjpuebla.trials.core.personas.JuezRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "materia", required = false) String materia,
            @RequestParam(value = "estatus", required = false) Estado estatus) {
        return this.juzgadoService.getAll(key, nombre, materia, estatus, pageable);
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

    @GetMapping("/allJuzgados")
    public List<JuzgadoRecordItem> findbyEstadoActiveAndInactive() {
        return this.juzgadoService.findbyEstadoActiveAndInactive();
    }

    @GetMapping("/active")
    public List<JuzgadoRecordItem> findAllByEstadoActive() {
        return this.juzgadoService.findAllByEstadoActive();
    }

    @PatchMapping("/{id}/status/{status}")
    public JuzgadoRecordItem updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.juzgadoService.updateStatus(id, status);
    }

    @GetMapping("/salas")
    public List<JuzgadoRecordItem> getSalas() {
        return this.juzgadoService.findAllByInstancia(InstanciaJuzgado.SEGUNDA_INSTANCIA);
    }

    @GetMapping("/salas/active")
    public List<JuzgadoRecordItem> getSalasActivas() {
        return this.juzgadoService.findSalasActivas();
    }

    @GetMapping("/actual")
    public List<JuzgadoRecordItem> getJuzgadoActual() {
        return Collections.singletonList(this.juzgadoService.getJuzgadoActual());
    }

    @GetMapping("/ponencias/{materiaId}")
    public List<JuzgadoRecordItem> getPonenciasDisponibles(@PathVariable Integer materiaId) {
        return this.juzgadoService.getPonenciasDisponibles(materiaId);
    }

    @GetMapping("/jueces")
    public List<JuezRecord> getJuecesPenales() {
        return this.juzgadoService.getJuecesPenales();
    }

    @GetMapping("/select")
    public List<JuzgadoVisitaduriaRecord> getJuzgados(
            @RequestParam(required = false) Integer materiaId,
            @RequestParam(required = false) Integer distritoId
    ) {
        return juzgadoService.getJuzgadosFiltrados(materiaId, distritoId);
    }

    @GetMapping(value = "/filtrar", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JuzgadoRecordItem> getJuzgadosFiltrados(
            @RequestParam(required = false) Integer materiaId,
            @RequestParam(required = false) Integer distritoId
    ) {
        return juzgadoService.findJuzgadosFiltrados(materiaId, distritoId);
    }
}
