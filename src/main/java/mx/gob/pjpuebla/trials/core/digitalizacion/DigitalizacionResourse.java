package mx.gob.pjpuebla.trials.core.digitalizacion;

import java.io.IOException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

@RequiredArgsConstructor
@RestController
@RequestMapping("/digitalizacion")
@SecurityRequirement(name = "Keycloak")
public class DigitalizacionResourse {

    private final DigitalizacionService digitalizacionService;

    @PostMapping("/uploadDocument") //digitalización
    public String uploadDocument(
            @RequestParam("file") MultipartFile file, 
            @RequestBody Documento doc) throws IOException {

        digitalizacionService.procesarArchivo(file, doc);
        
        //id y el nombre del documento que se encuentra
        return "Archivo subido exitosamente";
    }
}
