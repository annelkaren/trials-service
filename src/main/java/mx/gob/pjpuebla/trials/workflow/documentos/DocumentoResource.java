package mx.gob.pjpuebla.trials.workflow.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.PATCH;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final DocumentoService documentoService;

    @GetMapping("/bandeja/entrada")
    public Page<DocumentoGridRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "folio", required = false) String folio,
            @RequestParam(value = "expediente", required = false) String expediente,
            @RequestParam(value = "materia", required = false) String materia) {
        Documento documento = new Documento();
        documento.setJuzgado(new Juzgado().setMateria(new Materia().setNombre(materia)));
        documento.setFolio(folio).setExpediente(expediente);
        return this.documentoService.getAll(documento, pageable);
    }

    @PatchMapping("/bandeja/estatus/{id}")
    public DocumentoRecord updateStatus(@PathVariable Integer id, Map<String, Object> fields) {
        return this.documentoService.updateStatus(id, fields);
    }

    @PostMapping("/demanda")
    public DocumentoRecord createDemanda(@RequestBody DocumentoDTO documentoDTO) {
        return this.documentoService.createDemanda(documentoDTO);
    }

    @GetMapping(value = "/documentos/{id}/sello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
        return ResponseEntity.ok().headers(headers).body(selloGenerator.exportToPdf(id));
    }
}
