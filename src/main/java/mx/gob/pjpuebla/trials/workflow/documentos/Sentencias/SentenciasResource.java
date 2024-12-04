package mx.gob.pjpuebla.trials.workflow.documentos.Sentencias;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Sentencias.records.SentenciaRecordSave;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/worflow")
@SecurityRequirement(name = "keycloak")
public class SentenciasResource {

    private final SentenciasService sentenciasService;

    @PostMapping("/documentos/crearSentencia")
    public DocumentoGenericRecord crearSentencia(@RequestBody SentenciaRecordSave sentencia){
        return sentenciasService.crearSentencia(sentencia);
    }
    
}
