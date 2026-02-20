package mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios.records.AnexoOficioRecord;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "keycloak")
public class AnexoOficioResource {

    private final AnexoOficioService anexoOficioService;

    @PostMapping(value = "/oficios/anexos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AnexoOficioRecord> uploadAnexosOficio(
            @RequestParam("documentoId") Integer documentoId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return anexoOficioService.guardarAnexos(documentoId, files);
    }

    @GetMapping("/oficios/anexos/{documentoId}")
    public List<AnexoOficioRecord> getAnexosOficio(@PathVariable Integer documentoId) {
        return anexoOficioService.getAnexosByOficio(documentoId);
    }

    @GetMapping(value = "/oficios/anexos/file/{anexoId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAnexoOficioFile(@PathVariable Integer anexoId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("anexo_oficio", anexoId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(anexoOficioService.getAnexoFile(anexoId));
    }
}
