package mx.gob.pjpuebla.trials.workflow.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.sello.CaratulaGenerator;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final CaratulaGenerator caratulaGenerator;
    private final DocumentoService documentoService;
    private final DigitalizacionService digitalizacionService;

    @PostMapping("/demanda")
    public DocumentoRecord createDemanda(@RequestBody DocumentoDTO documentoDTO) {
        return this.documentoService.createDemanda(documentoDTO);
    }

//    @PatchMapping(value = "/demanda/{id}/anexos", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<AnexoRecord> editarAnexos(@PathVariable Integer id, @RequestBody AnexoRecord anexoRecord ){
//        AnexoRecord updatedAnexos = documentoService.editarAnexos(id, anexoRecord.anexos(), anexoRecord.motivoEdita());
//        return ResponseEntity.ok(updatedAnexos);
//    }


//    @GetMapping(value = "/demanda/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<DocumentoResponseRecord> getEditDocumento(@PathVariable Integer id) {
//        DocumentoResponseRecord  editDocumento  = documentoService.getEditDocumentoAnexo(id);
//        return  ResponseEntity.ok(editDocumento);
//    }

//    @GetMapping(value = "/documentos/{id}/sello", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
//        return ResponseEntity.ok().headers(headers).body(selloGenerator.exportToPdf(id));
//    }

//    @PostMapping("/documentos/digitalizacion/{documentoId}")
//    public DigitalizacionRecord digitizationDocument(
//            @RequestParam("file") MultipartFile file,
//            @PathVariable("documentoId") Integer documentoId) {
//        return digitalizacionService.procesarArchivo(file, documentoId);
//    }

//    @GetMapping(value = "/documentos/digitalizacion/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<byte[]> getFile(@PathVariable Integer documentoId) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("sello", documentoId + "_documento.pdf");
//        return ResponseEntity.ok().headers(headers).body(digitalizacionService.getDocumento(documentoId));
//    }


//    @GetMapping(value = "/documentos/{id}/caratula", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<byte[]> exportCaratulaPdf(@PathVariable Integer id) throws JRException, IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("caratula", id + "_caratula.pdf");
//        return ResponseEntity.ok().headers(headers).body(caratulaGenerator.exportToPdf(id));
//    }

//    @GetMapping("/bandeja/entrada")
//    public Page<DocumentoGridRecord> getAll(
//            @PageableDefault(size = 20) Pageable pageable,
//            @RequestParam(value = "key", required = false) String key) {
//        return this.documentoService.getAll(key, pageable);
//    }

    @PatchMapping("/bandeja/{id}/status/{status}")
    public DocumentoRecord updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.documentoService.updateStatus(id, status);
    }
}
