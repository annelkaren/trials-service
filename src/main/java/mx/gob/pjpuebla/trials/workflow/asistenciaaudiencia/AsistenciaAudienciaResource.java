package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.records.RegistrarAsistenciaAudienciaRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/audienciaasistencia")
@SecurityRequirement(name = "Keycloak")
public class AsistenciaAudienciaResource {

    private final AsistenciaAudienciaService asistenciaAudienciaService;

    @GetMapping
    public Page<AsistenciaAudienciaResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return this.asistenciaAudienciaService.getAll(pageable);
    }

    @PostMapping(value = "/registrarasistencia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AsistenciaAudiencia registrarAsistencia(
            @RequestPart("registrarAsistenciaAudienciaRecord") String registrarAsistenciaAudienciaRecordJson,
            @RequestPart("file") MultipartFile file) throws JsonProcessingException {

         RegistrarAsistenciaAudienciaRecord asistenciaAudienciaRecord = new ObjectMapper().readValue(registrarAsistenciaAudienciaRecordJson, RegistrarAsistenciaAudienciaRecord.class);
         return this.asistenciaAudienciaService.registrarAsistencia(asistenciaAudienciaRecord, file);
    }

    @GetMapping(value = "/documento-identificacion", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getDocumentoIdentificacion(
            @RequestParam Integer personaDocumentoId,
            @RequestParam Integer audienciaId) throws IOException {
        byte[] documento = this.asistenciaAudienciaService.getDocumentoAsistencia(personaDocumentoId, audienciaId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "documento_identificacion.pdf");
        return ResponseEntity.ok().headers(headers).body(documento);
    }
}
