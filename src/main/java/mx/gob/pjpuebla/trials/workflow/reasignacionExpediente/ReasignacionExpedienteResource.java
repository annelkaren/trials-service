package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReactivacionExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReasignacionExpedienteResponseRecord;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name="Keycloak")
public class ReasignacionExpedienteResource {

    private final ReasignacionExpedienteService reasignacionExpedienteService;

    @PostMapping("/reasignacionExpediente")
    public ReasignacionExpedienteResponseRecord reasignarExpediente(@RequestBody Map<String, Integer> data) {
        Integer carpetaParentId = data.get("carpetaParentId");
        
        return reasignacionExpedienteService.reasignarExpediente(carpetaParentId);
    }

    @PatchMapping("/reactivacionExpediente/{carpetaId}")
    public ReactivacionExpedienteRecord reactivacionExpediente(@PathVariable Integer carpetaId){
        return reasignacionExpedienteService.reactivacionExpediente(carpetaId);
    }

}
