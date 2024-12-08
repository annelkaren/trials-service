package mx.gob.pjpuebla.trials.workflow.documentos.Sentencias;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Sentencias.records.SentenciaRecordSave;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;
import org.springframework.web.bind.annotation.PutMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "keycloak")
public class SentenciasResource {

    private final SentenciasService sentenciasService;

    @PostMapping("/documentos/crearSentencia")
    public DocumentoGenericRecord crearSentencia(@RequestBody SentenciaRecordSave sentencia){
        return sentenciasService.save(sentencia);
    }

    @PutMapping("/documentos/actualizarSentencia")
    public DocumentoGenericRecord putMethodName(@RequestBody SentenciaRecordSave sentencia) {
        
        return sentenciasService.update(sentencia);
    }
    
    @PostMapping("/documentos/publicarSentencia")
    public DocumentoGenericRecord publicarSentencia(@RequestBody SentenciaRecordSave sentencia){
        return sentenciasService.publicarSentencia(sentencia);
    }
}
