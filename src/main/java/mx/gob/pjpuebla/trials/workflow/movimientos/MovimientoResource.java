package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mx.gob.pjpuebla.trials.error.ErrorRecord;
import mx.gob.pjpuebla.trials.util.enums.DevolucionMotivo;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;

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

    @GetMapping
    public List<MotivoDevolucionRecord> listaMotivos(){
        return Arrays.stream(DevolucionMotivo.values())
            .map(motivo -> new MotivoDevolucionRecord(motivo.getId(), motivo.getNombre()))
            .collect(Collectors.toList());
    }

    @PostMapping("/turnado")
    public ResponseEntity<String> crearMotivo(@RequestBody MotivoRecord motivoRecord) {
        try {
            movimientoService.createMotivo(motivoRecord);
            return ResponseEntity.ok("Movimiento creado y estatus actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el movimiento: " + e.getMessage());
        }
    }
}
