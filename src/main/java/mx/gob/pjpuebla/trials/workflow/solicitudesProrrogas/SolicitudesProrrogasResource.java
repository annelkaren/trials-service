package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/solicitudesProrrogas")
@SecurityRequirement(name = "keycloak")
public class SolicitudesProrrogasResource {

    public final SolicitudesProrrogasService solicitudesProrrogasService;

    @PostMapping("/solicitudProrroga")
    public ResponseEntity<?> solicitarProrroga(@RequestBody SolicitudesProrrogasRecord solicitudesProrrogaRecord) {
        return solicitudesProrrogasService.solicitarProrroga(solicitudesProrrogaRecord);
    }
}
