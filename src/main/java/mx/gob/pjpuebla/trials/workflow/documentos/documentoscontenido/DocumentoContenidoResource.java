package mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/")
@SecurityRequirement(name = "Keycloak")
public class DocumentoContenidoResource {

    private final DocumentoContenidoService documentoContenidoService;
    
    @GetMapping(value = "documentoContenido/{documentoId}")
    public DocumentoOficioDigitalizacionRecord getDataDocumentoDigitalizacion(@PathVariable Integer documentoId){
        return documentoContenidoService.getDataDocumentoDigitalizacion(documentoId);
    }

    @PatchMapping(value = "documentoContenido/{documentoId}")
    public Integer cancelarOficio(@PathVariable Integer documentoId){
        return documentoContenidoService.cancelarOficio(documentoId);
    }

    @PutMapping("documentoContenido")
    public DocumentoOficioDigitalizacionRecord updateDocumentoOficioDigitalizacion(@RequestBody DocumentoOficioDigitalizacionRecord documento){
        return documentoContenidoService.updateDocumentoOficioDigitalizacion(documento);
    }

    @PatchMapping("/documentoContenido/{documentoId}/status/{status}")
    public Integer publicarCancelarOficio(@PathVariable Integer documentoId, @PathVariable char status) {
                
        return documentoContenidoService.publicarCancelarOficio(documentoId, status);
    }

}
