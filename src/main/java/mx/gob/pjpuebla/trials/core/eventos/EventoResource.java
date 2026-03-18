package mx.gob.pjpuebla.trials.core.eventos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoEditRecord;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoPeriodosRecord;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<EventoRecord> getEventosGenerales() {
        return this.eventoService.getEventosGenerales();
    }

    @GetMapping("/oficialiacomun")
    public List<EventoRecord> getEventosOficialiaComun() {
        return this.eventoService.getEventosOficialiaComun();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        eventoService.deleteById(id);
    }

    @PutMapping("/editarperiodo")
    public EventoRecord editarEventoPeriodo(@RequestBody EventoEditRecord record) {
        return this.eventoService.editarEventoPeriodo(record);
    }

    @GetMapping("/diainhabil")
    public Boolean validarDiaInhabil(@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fecha) {
        return this.eventoService.validarDiaInhabil(fecha);
    }
}
