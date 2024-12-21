package mx.gob.pjpuebla.trials.workflow.transferencias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecord;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecordResponse;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/transferencias")
@SecurityRequirement(name = "Keycloak")
public class TransferenciaResource {
    private final TransferenciaServices transferenciaServices;

    @GetMapping("/registro")
    public TransferenciaRecordResponse getTransferencia(){
        return transferenciaServices.getTransferenciaByPersonaEntrega();
    }

    @GetMapping("/download/{uuid}")
    public TransferenciaRecordResponse getFileTransferencia(@PathVariable String uuid){
        return transferenciaServices.getTransferencia(uuid);
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

