package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/historico")
    public Page<BandejaEntradaResponse> getHistorico(
            @ModelAttribute BandejaEntradaFilter filtros,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.archivoJudicialService.getHistorico(filtros, pageable);
    }

    @PostMapping
    public String recibirExpedientes(@RequestBody List<RecibirExpedienteRecord> list) {
        return archivoJudicialService.recibirExpedientes(list);
    }
}
