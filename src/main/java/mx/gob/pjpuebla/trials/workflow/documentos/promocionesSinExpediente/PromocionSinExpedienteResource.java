package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteFiltrosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedientePageRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteSaveRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteSearchRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoPromocionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/promocionesSinExpedientes/")
@SecurityRequirement(name = "keycloak")
public class PromocionSinExpedienteResource {

    private final PromocionSinExpedienteService promocionSinExpedienteService;
    private final SelloGenerator selloGenerator;
    
    @PostMapping("")
    public ResponseEntity<ApiResponse<PromocionSinExpedienteSaveRecord>> guardarPromocion(@RequestBody PromocionSinExpedienteRecord data) {
        ApiResponse<PromocionSinExpedienteSaveRecord> response = promocionSinExpedienteService.save(data);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("")
    public Page<PromocionSinExpedientePageRecord> getAllPromocionesSinExpediente(@ModelAttribute PromocionSinExpedienteFiltrosRecord filtros, Pageable pageable) {
        return promocionSinExpedienteService.getAll(filtros, pageable);
    }
    
    @GetMapping("sello/{id}")
    public ResponseEntity<byte[]> getSelloPromocionSinExp(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
        return ResponseEntity.ok().headers(headers).body(selloGenerator.getSelloPromocionSinExpediente(id));
    }

    @PostMapping("asociarExpediente/{id}")
    public DocumentoPromocionResponseRecord asociarExpediente(@PathVariable Integer id) {
       
        return promocionSinExpedienteService.asociarExpediente(id);
    }
    

}
