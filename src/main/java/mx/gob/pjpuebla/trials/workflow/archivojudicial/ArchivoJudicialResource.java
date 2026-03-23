package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.records.BandejaHistorialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/archivojudicial")
@SecurityRequirement(name = "Keycloak")
public class ArchivoJudicialResource {

    private final ArchivoJudicialService archivoJudicialService;

    @GetMapping
    public Page<ArchivoJudicialRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return this.archivoJudicialService.getAll(pageable);
    }

    @GetMapping("/recibidos")
    public Page<RecibidosRecord> getRecibidos(
            @PageableDefault(size = 20) Pageable pageable) {
        return this.archivoJudicialService.getRecibidos(pageable);
    }

    @GetMapping("/solicitudes")
    public Page<SolicitudesRecord> getSolicitudes(
            @PageableDefault(size = 20) Pageable pageable) {
        return this.archivoJudicialService.getSolicitudes(pageable);
    }

    @GetMapping(value = "/historico", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<BandejaHistorialRecord> getAllHistorial(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "folio", required = false) String folio,
            @RequestParam(value = "expediente", required = false) String expediente,
            @RequestParam(value = "materia", required = false) String materia,
            @RequestParam(value = "tipoEntrada", required = false) String tipoEntrada,
            @RequestParam(value = "organoJurisdiccional", required = false) String organoJurisdiccional,
            @RequestParam(value = "fechaFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFrom,
            @RequestParam(value = "fechaTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaTo) {
        return this.archivoJudicialService.getAllHistorial(pageable, key, folio, expediente, materia, tipoEntrada, organoJurisdiccional, fechaFrom, fechaTo);
    }

    @PostMapping
    public String recibirExpedientes(@RequestBody List<RecibirExpedienteRecord> list) {
        return archivoJudicialService.recibirExpedientes(list);
    }

    @PostMapping("/cancelar/{tipo}/{id}")
    public boolean cancelarSolicitud(@PathVariable String tipo, @PathVariable Integer id) {
        return archivoJudicialService.cancelarExpediente(tipo, id);
    }

    @PostMapping("/devolver/{tipo}/{id}")
    public boolean devolverExpediente(@PathVariable String tipo, @PathVariable Integer id) {
        return archivoJudicialService.devolverExpediente(tipo, id);
    }
}
