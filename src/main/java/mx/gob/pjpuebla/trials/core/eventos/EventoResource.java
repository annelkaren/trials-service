package mx.gob.pjpuebla.trials.core.eventos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoEditRecord;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoPeriodosRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/evento")
@SecurityRequirement(name = "Keycloak")
public class EventoResource {

    private final EventoService eventoService;

    @PostMapping(value = "/periodos")
    public Evento createPeriodos(@RequestBody EventoPeriodosRecord record) {
        return this.eventoService.createPeriodos(record);
    }

    @GetMapping("/generales")
    public Page<EventoRecord> getEventosGenerales(@PageableDefault Pageable pageable) {
        return this.eventoService.getEventosGenerales(pageable);
    }

    @GetMapping("/oficialiacomun")
    public Page<EventoRecord> getEventosOficialiaComun(Pageable pageable) {
        return this.eventoService.getEventosOficialiaComun(pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        eventoService.deleteById(id);
    }

    @PutMapping("/editarperiodo")
    public EventoRecord editarEventoPeriodo(@RequestBody EventoEditRecord record) {
        return this.eventoService.editarEventoPeriodo(record);
    }
}
