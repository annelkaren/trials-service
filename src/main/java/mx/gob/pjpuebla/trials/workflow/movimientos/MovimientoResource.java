package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mx.gob.pjpuebla.trials.error.ErrorRecord;
import mx.gob.pjpuebla.trials.util.enums.DevolucionMotivo;

import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;


import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/movimientos")
@SecurityRequirement(name = "Keycloak")
public class MovimientoResource {

    private final MovimientoService movimientoService;
    private final MovimientoReporteGenerator generator;
    private final AnexoRepository anexoRepository;

    @GetMapping("/{uuid}")
    public ResponseEntity<Object> getReporteMovimiento(@PathVariable("uuid") String uuid) {
        
        List<MovimientoSalidaRecord> movimientos = movimientoService.getMovimientosSalida(uuid);
        // Combinar movimientos con anexos
        List<MovimientoSalidaRecord> movimientosConAnexos = movimientos.stream()
                .map(movimiento -> {
                    // Obtener los anexos relacionados con este movimiento
                    List<AnexoBandejaRecepcionRecord> anexos = anexoRepository.findAnexosByCarpetaIdOrDocumentoId(
                            movimiento.documentoId(), 
                            movimiento.carpetaId()   
                    );

                    // Crear una nueva instancia de MovimientoSalidaRecord con los anexos
                    return new MovimientoSalidaRecord(
                            movimiento.uuid(),
                            movimiento.tipoCarpeta(),
                            movimiento.folio(),
                            movimiento.expediente(),
                            movimiento.fecha(),
                            movimiento.juzgado(),
                            movimiento.data(),
                            movimiento.documentoFolio(),
                            movimiento.tipoDocumento(),
                            movimiento.expedienteDoc(),
                            movimiento.oficialia(),
                            movimiento.responsable(),
                            movimiento.observaciones(),
                            movimiento.documentoId(),
                            movimiento.carpetaId(),
                            anexos // Anexos obtenidos en la segunda consulta
                    );
                })
                .collect(Collectors.toList());
      
        HttpHeaders headers = new HttpHeaders();

        if (movimientos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND_404).body(new ErrorRecord("ListaSalida","No hay registros"));
        }

        try{
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("reporte", "salida_"+uuid + ".pdf");

            byte[] reporte = generator.getReporteSalida(movimientosConAnexos);

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

    @GetMapping("/turnado/{carpetaId}")
    public List<TurnadoMovimientoRecord> turnadoList(@PathVariable("carpetaId") Integer carpetaId){
        return movimientoService.getTurnadoMovimientos(carpetaId);
    }

    @PutMapping("/solicitudProrroga/{movimientoId}")
    public ResponseEntity<?> solicitarProrroga(@PathVariable Integer movimientoId, @RequestBody MovimientoProrrogaRecord movimientoProrrogaRecord) {
        return movimientoService.solicitarProrroga(movimientoId, movimientoProrrogaRecord);
    }

}
