package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/worflow")
@SecurityRequirement(name="Keycloak")
public class ReasignacionExpedienteResource {


    

}
