package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;


import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.DetallesPruebasRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/audiencias-pruebas")
@SecurityRequirement(name = "Keycloak")

public class AudienciaPruebasResource {
    @Autowired
    private AudienciaPruebasService audienciasPruebasService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createAudienciaPrueba(
            @RequestPart("audienciaPruebaRequest") String audienciaPruebaRequestJson,
            @RequestPart("file") Optional<MultipartFile> file) throws JsonProcessingException {

        AudienciaPruebaRequestRecord audienciaPruebaRequest = new ObjectMapper().readValue(audienciaPruebaRequestJson, AudienciaPruebaRequestRecord.class);
        audienciasPruebasService.createAudienciaPrueba(audienciaPruebaRequest, file.orElse(null));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Page<DetallesPruebasRecord> getAllByAudiencia(
            @PathVariable(value = "id", required = false) Integer id,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.audienciasPruebasService.getAudienciaPruebasByAudiencia(id, pageable);
    }

    @PostMapping("/{id}")
    public void updateDesistimientoAdmision(@PathVariable Integer id, @RequestBody String desAdm) {
        audienciasPruebasService.pachDesistimientoAdmision(desAdm, id);
    }


    @GetMapping(value = "/pruebaDoc/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer id) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("prueba", id + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(audienciasPruebasService.getAudienciaPurebasDocumento(id.longValue()));
    }

}
