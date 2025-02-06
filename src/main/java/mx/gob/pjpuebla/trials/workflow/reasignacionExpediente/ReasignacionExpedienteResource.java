package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReasignacionExpedienteResponseRecord;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RequiredArgsConstructor
@RestController
@RequestMapping("/api/worflow/reasignacionExpediente")
@SecurityRequirement(name="Keycloak")
public class ReasignacionExpedienteResource {

    private final ReasignacionExpedienteService reasignacionExpedienteService;

    @PostMapping
    public ReasignacionExpedienteResponseRecord reasignarExpediente(@PathParam("carpetaParentId") Integer carpetaParentId) {
        return reasignacionExpedienteService.reasignarExpediente(carpetaParentId);
    }
    
    

}
