package mx.gob.pjpuebla.trials.workflow.transferencias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.ErrorRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoReporteGenerator;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecord;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecordResponse;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/transferencias")
@SecurityRequirement(name = "Keycloak")
public class TransferenciaResource {
    private final TransferenciaService transferenciaServices;
    private final MovimientoReporteGenerator movimientoReporteGenerator;

    @GetMapping("/registro")
    public TransferenciaRecordResponse getTransferencia(){
        return transferenciaServices.getTransferenciaByPersonaEntrega();
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<Object> getFileTransferencia(@PathVariable String uuid){
        TransferenciaRecordResponse recordResponse = transferenciaServices.getTransferencia(uuid);

         HttpHeaders headers = new HttpHeaders();

        if (recordResponse.expedientes().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND_404).body(new ErrorRecord("ListaTransferencia","No hay registros"));
        }

        try{
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("reporte", "transferencia_"+uuid + ".pdf");

            byte[] reporte = movimientoReporteGenerator.getReporteTransferencia(recordResponse);

            return ResponseEntity.ok().headers(headers).body(reporte);
        }catch (IOException | JRException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR_500).body(new ErrorRecord("ListaTransferencia", e.getMessage()));
        }
        
    }

    @PostMapping
    public TransferenciaRecordResponse crear(@RequestBody TransferenciaRecord transferenciaRecord){
        return transferenciaServices.create(transferenciaRecord);
    }

    @PutMapping("/{id}")
    public TransferenciaRecordResponse actualizar(@PathVariable Integer id, @RequestBody TransferenciaRecord transferenciaRecord){
        return transferenciaServices.update(transferenciaRecord, id);
    }
}

