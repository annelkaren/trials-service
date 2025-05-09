package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnvioRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosCambioEstatus;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;

import org.springframework.http.HttpHeaders;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "keycloack")
public class BandejaEnviosResource {

    private final BandejaEnviosService bandejaEnviosService;
    private final DigitalizacionService digitalizacionService;

    @GetMapping("/bandejaEnvios/")
    public Page<BandejaEnviosRecord> obtenerBandejaEnvios(Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return bandejaEnviosService.getAllBandejaEnviados(key, pageable);
    }

    @PostMapping("/bandejaEnvios/")
    public BandejaEnvioRecordResponse actualizarEstatusOficio(@RequestBody BandejaEnviosCambioEstatus bandejaEnvios) {

        return bandejaEnviosService.actualizarEstatusOficio(bandejaEnvios);
    }

    @PostMapping("/bandejaEnvios/digitalizacion")
    public BandejaEnvioRecordResponse digitalizarAcuseOficioOCP(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentoId") Integer documentoId) {
        return bandejaEnviosService.digitalizarAcuseOficioOCP(file, documentoId);
    }

    @GetMapping(value = "/bandejaEnvios/digitalizacion/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer documentoId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("acuse", documentoId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(digitalizacionService.getDocumento(documentoId));
    }


}
