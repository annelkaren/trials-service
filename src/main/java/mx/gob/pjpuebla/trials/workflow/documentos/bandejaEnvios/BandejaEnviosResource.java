package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnvioRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosCambioEstatus;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name="keycloack")
public class BandejaEnviosResource {
    
    private final BandejaEnviosService bandejaEnviosService;

    @GetMapping("/bandejaEnvios")
    public Page<BandejaEnviosRecord> obtenerBandejaEnvios(Pageable pageable, @RequestParam(value = "key", required = false) String key) {
        return bandejaEnviosService.getAllBandejaEnviados(key, pageable);
    }

    @PostMapping("/bandejaEnvios")
    public BandejaEnvioRecordResponse actualizarEstatusOficio(@RequestBody @Valid BandejaEnviosCambioEstatus bandejaEnvios) {
        return bandejaEnviosService.actualizarEstatusOficio(bandejaEnvios);
    }

}
