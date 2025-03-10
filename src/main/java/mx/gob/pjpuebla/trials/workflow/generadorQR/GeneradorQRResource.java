package mx.gob.pjpuebla.trials.workflow.generadorQR;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workflow")
@SecurityRequirement(name="keycloak")
public class GeneradorQRResource {
    
    private final GeneradorQRService generadorQRService;

    @GetMapping("/generarQR/{expMin}/{expMax}/{year}")
    public ResponseEntity<byte[]> getDocumentoQR(@PathVariable Integer expMin, @PathVariable Integer expMax, @PathVariable Integer year) throws JRException, IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("qr", expMin + "_" + expMax + "_" + year);
        
        return ResponseEntity.ok().headers(headers).body(generadorQRService.getContinuityReport(expMin, expMax, year));
    }

    @GetMapping("/generarQR/porCasilla/{expediente}/{year}/{casilla}")
    public ResponseEntity<byte[]> getDocumentoQRByCastilla(
        @PathVariable String expediente,
        @PathVariable Integer year, 
        @PathVariable Integer casilla) throws JRException, IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("qr",  expediente);
        
        return ResponseEntity.ok().headers(headers).body(generadorQRService.getReportByCoordinates(expediente + "/" + year, casilla));
    }
}
