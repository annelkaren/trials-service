package mx.gob.pjpuebla.trials.publico;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionesSalasServices;


@RestController
@RequiredArgsConstructor
@RequestMapping("/public/")
public class PublicResources {
    
    private NotificacionesSalasServices notificacionesSalasServices;

    @GetMapping(value = "notificaciones/download/{nombreArchivo}")
    public ResponseEntity<byte[]> downloadArchivoPublico(@PathVariable String nombreArchivo) {
        byte[] file = notificacionesSalasServices.downloadArchivoPublico(nombreArchivo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(nombreArchivo).build());
        return ResponseEntity.ok().headers(headers).body(file);
    }


}
