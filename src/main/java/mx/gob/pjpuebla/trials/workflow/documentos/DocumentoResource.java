package mx.gob.pjpuebla.trials.workflow.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.sello.OficioService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloCaratulaService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final SelloCaratulaService caratulaGenerator;
    private final DocumentoService documentoService;
    private final DigitalizacionService digitalizacionService;
    private final OficioService oficioService;

    @PostMapping("/demanda")
    public DocumentoRecord createDemanda(@RequestBody DocumentoSaveRecord documentoSaveRecord) {
        return this.documentoService.createDemanda(documentoSaveRecord);
    }

    @PatchMapping(value = "/demanda/{id}/anexos", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentoRecord editAnexos(@PathVariable Integer id, @RequestBody AnexoRecord anexoRecord) {
        return documentoService.editarAnexos(id, anexoRecord.anexos(), anexoRecord.motivoEdita());
    }

    @GetMapping(value = "/demanda/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentoResponseRecord> getDemandaById(@PathVariable Integer id) {
        DocumentoResponseRecord editDocumento = documentoService.getDemandaById(id);
        return ResponseEntity.ok(editDocumento);
    }

    @GetMapping(value = "/documentos/{id}/sello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
        return ResponseEntity.ok().headers(headers).body(selloGenerator.exportToPdf(id));
    }

    @PostMapping("/documentos/digitalizacion/{documentoId}")
    public DigitalizacionRecord digitizationDocument(
            @RequestParam("file") MultipartFile file,
            @PathVariable("documentoId") Integer documentoId) {
        return digitalizacionService.procesarArchivo(file, documentoId);
    }

    @GetMapping(value = "/documentos/digitalizacion/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer documentoId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", documentoId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(digitalizacionService.getDocumento(documentoId));
    }

    @GetMapping(value = "/documentos/{id}/caratula", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportCaratulaPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("caratula", id + "_caratula.pdf");
        return ResponseEntity.ok().headers(headers).body(caratulaGenerator.exportToPdf(id));
    }

    @GetMapping("/bandeja/entrada")
    public Page<DocumentoGridRecord> getAll(@PageableDefault(size = 20) Pageable pageable,
                                            @RequestParam(value = "key", required = false) String key) {
        return this.documentoService.getAll(key, pageable);
    }

    @GetMapping("/bandeja/salida")
    public Page<DocumentoSalidaResponseRecord> getAllBandejaSalida(@PageableDefault(size = 20) Pageable pageable,
                                                                   @RequestParam(value = "key", required = false) String key) {
        return this.documentoService.getAllBandejaSalida(key, pageable);
    }

    @PatchMapping("/bandeja/{id}/status/{status}")
    public DocumentoRecord updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.documentoService.updateStatus(id, status);
    }

    @GetMapping(value = "/bandeja/historial", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<DocumentoGridRecord> getAllHistorial(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "folio", required = false) String folio,
            @RequestParam(value = "expediente", required = false) String expediente,
            @RequestParam(value = "estatus", required = false) EstadoCarpeta estatus,
            @RequestParam(value = "tipoEntrada", required = false) String tipoEntrada,
            @RequestParam(value = "materiaNombre", required = false) String materiaNombre) {

        Carpeta carpeta = new Carpeta()
                .setFolio(folio)
                .setExpediente(expediente)
                .setEstatus(estatus);
        if (tipoEntrada != null) {
            carpeta.setTipoCarpeta(TipoCarpeta.valueOf(tipoEntrada));
        }
        if (materiaNombre != null) {
            Materia materia = new Materia();
            materia.setNombre(materiaNombre);

            Juzgado juzgado = new Juzgado();
            juzgado.setMateria(materia);

            carpeta.setJuzgado(juzgado);
        }
        return documentoService.getAllHistorial(pageable,
                new Documento().setCarpeta(carpeta));
    }

    @PostMapping("/documento/promocion")
    public DocumentoPromocionResponseRecord createPromocion(
            @RequestBody DocumentoPromocionRecord documentoPromocionRecord) {
        return this.documentoService.createPromocion(documentoPromocionRecord);
    }

    @PostMapping("/exhorto")
    public DocumentoRecord createExhorto(@RequestBody DocumentoExhortoRecord documentoExhortoRecord) {
        return documentoService.createExhorto(documentoExhortoRecord);
    }

    @PostMapping("/apelacion")
    public DocumentoRecord create(@RequestBody @Valid ApelacionRecord apelacionRecord) {
        return this.documentoService.createApelacion(apelacionRecord);
    }

    @GetMapping("/bandeja/recepcion")
    public Page<DocumentoBandejaRecepcionRecord> getAllBandejaRecepcion(
            @RequestParam(value = "key", required = false) String key,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.documentoService.getAllBandejaRecepcion(key, pageable);
    }

    @GetMapping("/bandeja/asignados")
    public Page<DocumentoAsignadoResponseRecord> getAllBandejaAsignados(
            @RequestParam(value = "key", required = false) String key,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.documentoService.getAllAsignado(key, pageable);
    }
    @PostMapping("/bandeja/salida")
    public String sendToBandejaRecepcion(@RequestBody @Valid SalidaSentToRecepcionRecord salidaSentToRecepcionRecord) {
        return this.documentoService.sendToBandejaRecepcion(salidaSentToRecepcionRecord.idList(), salidaSentToRecepcionRecord.personaCarrito());
    }


    @PostMapping("/oficio")
    public Integer generarOficio(@RequestBody DocumentoOficioRecord oficio) {
        return documentoService.createOficio(oficio.institucionId(), oficio.fechaEmision(), oficio.asunto(),
                oficio.carpetaId());
    }

    @GetMapping(value = "/documentos/indicadores", produces = MediaType.APPLICATION_JSON_VALUE)
    public IndicadoresRecord getIndicadores(@RequestParam Boolean isRecepcion) {
        return this.documentoService.getIndicadores();
    }

    @GetMapping("/bandeja/recepcion/anexos/{id}")
    public DocumentoRecepcionRecord getDataDocumentoRecepcion(@PathVariable Integer id) {
        return documentoService.getDataDocumentoRecepcion(id);
    }

    @GetMapping(value = "/documentos/oficio/{formato}/{oficioId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable boolean formato, @PathVariable Integer oficioId) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("oficio", formato + "_" + oficioId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(oficioService.getOficio(formato, oficioId));
    }

    @GetMapping(value = "/oficio/digitalizacion/{documentoId}")
    public DocumentoOficioDigitalizacionRecord getDataDocumentoDigitalizacion(@PathVariable Integer documentoId){
        return documentoService.getDataDocumentoDigitalizacion(documentoId);
    }

    @PatchMapping(value = "/oficio/digitalizacion/{documentoId}")
    public Integer cancelarOficio(@PathVariable Integer documentoId){
        return documentoService.cancelarOficio(documentoId);
    }

    @PutMapping(value = "/oficio/digitalizacion")
    public DocumentoOficioDigitalizacionRecord updateDocumentoOficioDigitalizacion(@RequestBody DocumentoOficioDigitalizacionRecord documento){
        return documentoService.updateDocumentoOficioDigitalizacion(documento);
    }
}
