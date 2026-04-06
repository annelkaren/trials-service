package mx.gob.pjpuebla.trials.workflow.audiencias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.eventos.EventoService;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.*;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class AudienciaResource {

    private final AudienciaService audienciaService;
    private final JuzgadoRepository juzgadoRepository;
    private final EventoService eventoService;

    @GetMapping("/bandeja/audienciasgenerales")
    public Page<AudienciasGeneralesResponseRecord> getAllAudienciasGenerales(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "tipoAudiencia", required = false) String tipoAudiencia,
            @RequestParam(value = "juez", required = false) String juez,
            @RequestParam(value = "numCarpeta", required = false) String numCarpeta,
            @RequestParam(value = "lugar", required = false) String lugar,
            @RequestParam(value = "estatus", required = false) String estatus,
            @RequestParam(value = "fechaFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFrom,
            @RequestParam(value = "fechaTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaTo,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.audienciaService.getAllAudienciasGenerales(
                key, tipoAudiencia, juez, numCarpeta, lugar, estatus, fechaFrom, fechaTo, pageable);
    }

    @GetMapping("/participantes/audiencia/{audienciaId}/carpeta/{carpetaId}")
    public List<AsistenciaPersonaDocumento> getParticipantesAudiencia(@PathVariable Integer audienciaId, @PathVariable Integer carpetaId) {
        return this.audienciaService.getParticipantesAudiencia(carpetaId, audienciaId);
    }
    

    @DeleteMapping("/bandeja/audienciasgenerales/{id}")
    public void delete(@PathVariable Integer id) {
        this.audienciaService.deleteAudiencia(id);
    }

    @GetMapping(value = "/audiencias/motivos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CarpetaCatalogoRecord> getAudienciasMotivos(){
        return this.audienciaService.getAudienciasMotivos();
    }

    @PatchMapping("/bandeja/audienciasgenerales/diferir/{id}")
    public void diferir(@PathVariable Integer id) {
        this.audienciaService.diferirAudiencia(id);
    }

    @PostMapping("/audiencias/crearAudiencias")
    public AudienciasResponseRecord create(@RequestBody AudienciaSaveRecord audiencia) {
        return this.audienciaService.createAudiencia(audiencia);
    }

    @GetMapping(value = "/audiencias/estatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getEstatusAudiencias() {
        return this.audienciaService.getEstatusAudiencias();
    }

    @PostMapping("/audiencias/horaInicio")
    public void setHoraAudiencias(@RequestBody SetHorasRecord setHorasRecord) {
        this.audienciaService.setHoraAudiencias(setHorasRecord.idAudiencia(), setHorasRecord.hora(), setHorasRecord.isInicio());
    }

    @PatchMapping("/audiencias/tabGeneral")
    public void audienciaTabGeneral(@RequestBody AudienciaTabGeneralRecord audienciaTabGeneralRecord
    ) {
        this.audienciaService.audienciaTabGeneral(audienciaTabGeneralRecord);
    }

    @PostMapping("/audiencias/tabGeneral/{audienciaId}")
    public void audienciaDigitalizacionActaMinima(@RequestParam("file") MultipartFile file,
                                                  @PathVariable("audienciaId") Integer audienciaId
    ) {
        this.audienciaService.guardarArchivo(file, audienciaId);
    }


    @GetMapping(value = "/audiencias/tabGeneral/{audienciaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer audienciaId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("actaMinima", audienciaId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(audienciaService.getAudienciaDocumento(audienciaId));
    }


    @PatchMapping("/bandeja/audienciasgenerales/reprogramar")
    public AudienciasResponseRecord reprogramarAudiencia(@RequestBody ReprogramarAudienciaRecord audiencia) {
        return this.audienciaService.reprogramarAudiencia(audiencia);
    }

    @GetMapping("/audiencias/getAgenda/{salaId}")
    public List<AudienciaAgendaRecord> getAgenda(@PathVariable Integer salaId) {
        return this.audienciaService.getAgendaSala(salaId);
    }
    
    @PostMapping("/audiencias/validarDisponibilidad")
    public ResponseEntity<String> validarDisponibilidad(@RequestBody ValidarDisponibilidadRequestRecord request) {
        boolean disponible = audienciaService.validarDisponibilidad(request);

        if (!disponible) {
            return ResponseEntity.badRequest().body("Audiencia en conflicto");
        }

        return ResponseEntity.ok("La sala está disponible");
    }

    @GetMapping("/audiencias/getDiaInhabil")
    public ResponseEntity<Boolean> esDiaInhabil(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("juzgadoId") Integer juzgadoId) {

            Juzgado juzgado = juzgadoRepository.findById(juzgadoId)
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", juzgadoId.toString()));
        
        Boolean esInhabil = eventoService.esDiaInHabil(fecha, juzgado, null);

        return ResponseEntity.ok(esInhabil);
    }

    @GetMapping("/audiencias/programadas/{carpetaId}")
    public List<AudienciaProgramadaRecord> getAudienciasProgramadas(@PathVariable Integer carpetaId) {
        return audienciaService.getAudienciasProgramadas(carpetaId);
    }
    
    
}
