package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.io.IOException;
import java.util.List;

import mx.gob.pjpuebla.trials.error.ErrorRecord;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/movimientos")
@SecurityRequirement(name = "Keycloak")
public class MovimientoResource {

    private final MovimientoService movimientoService;
    private final MovimientoReporteGenerator generator;

    @GetMapping("/{uuid}")
    public ResponseEntity<Object> getReporteMovimiento(@PathVariable("uuid") String uuid) {
        List<MovimientoSalidaRecord> movimientos = movimientoService.getMovimientosSalida(uuid);

        HttpHeaders headers = new HttpHeaders();

        if (movimientos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND_404).body(new ErrorRecord("ListaSalida","No hay registros"));
        }

        try{
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("reporte", "salida_"+uuid + ".pdf");

            byte[] reporte = generator.getReporteSalida(movimientos);

            return ResponseEntity.ok().headers(headers).body(reporte);
        }catch (IOException | JRException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR_500).body(new ErrorRecord("ListaSalida", e.getMessage()));
        }
        
    }
    
}
