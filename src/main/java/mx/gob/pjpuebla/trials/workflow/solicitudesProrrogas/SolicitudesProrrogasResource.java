package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
import org.springframework.web.bind.annotation.PutMapping;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/solicitudesProrrogas")
@SecurityRequirement(name = "keycloak")
public class SolicitudesProrrogasResource {

    public final SolicitudesProrrogasService solicitudesProrrogasService;

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<?>> solicitarProrroga(@RequestBody SolicitudesProrrogasRecord solicitudesProrrogaRecord) {
        return solicitudesProrrogasService.solicitarProrroga(solicitudesProrrogaRecord);
    }

    @GetMapping("/listar")
    public Page<SolicitudProrrogaRecordResponse> listarSolicitudesProrrogas(Pageable pageable,  @RequestParam(value = "key", required = false) String key) {
        return solicitudesProrrogasService.getSolicitudesProrrogas(key, pageable);
    }

    @PutMapping("/estatusProrroga")
    public ResponseEntity<ApiResponse<?>> estatusProrroga(@RequestBody List<SolicitudesProrrogasSaveRecord> solicitudesProrrogasSaveRecord) {
        return solicitudesProrrogasService.actualizaSolicitudProrroga(solicitudesProrrogasSaveRecord);
    }

    @GetMapping("/listasr/{movimientoId}")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    
}
