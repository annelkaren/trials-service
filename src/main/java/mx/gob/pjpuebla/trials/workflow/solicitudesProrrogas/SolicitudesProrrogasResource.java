package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/solicitudesProrrogas")

public class SolicitudesProrrogasResource {

    public final SolicitudesProrrogasService solicitudesProrrogasService;

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<?>> solicitarProrroga(@RequestBody SolicitudesProrrogasRecord solicitudesProrrogaRecord) {
        return solicitudesProrrogasService.solicitarProrroga(solicitudesProrrogaRecord);
    }

    @GetMapping("/listar")
    public Page<SolicitudProrrogaRecordResponse> listarSolicitudesProrrogas() {
        return solicitudesProrrogasService.getSolicitudesProrrogas();
    }

    @GetMapping("/listar/{movimientoId}")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    
}
