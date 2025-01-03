package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;

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
}
