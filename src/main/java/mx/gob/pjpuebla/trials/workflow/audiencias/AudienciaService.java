package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.salaPersona.SalaPersonaRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.AsistenciaAudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.*;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.etiquetas.Etiqueta;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AudienciaService {

        private static final String SALA_NOT_FOUND = "Sala no encontrada";
        private static final String AUDIENCIA_NOT_FOUND = "Audiencia no encontrada";
        private static final String TIPO_AUDIENCIA_NOT_FOUND = "Tipo audiencia no encontrada";
        private final AudienciaRepository audienciaRepository;
        private final SalaRepository salaRepository;
        private final BloqueRepository bloqueRepository;
        private final EtiquetaRepository etiquetaRepository;
        private final PersonaService personaService;
        private final TipoAudienciaRepository tipoAudienciaRepository;
        private final CarpetaRepository carpetaRepository;
        private final CarpetaDetalleRepository carpetaDetalleRepository;
        private final PersonaDocumentoRepository personaDocumentoRepository;
        private final AsistenciaAudienciaRepository asistenciaAudienciaRepository;
        private final SalaPersonaRepository salaPersonaRepository;
        private final DigitalizacionService digitalizacionService;

        public Audiencia create(SalaAudienciaRecord salaAudienciaRecord, TipoAudiencia tipoAudiencia, Carpeta carpeta) {
                Sala sala = salaRepository.findById(salaAudienciaRecord.id())
                                .orElseThrow(() -> new NotFoundException(SALA_NOT_FOUND,
                                                String.valueOf(salaAudienciaRecord.id())));
                Bloque bloque = bloqueRepository.findById(salaAudienciaRecord.bloqueId())
                                .orElseThrow(() -> new NotFoundException("Bloque no encontrado", "BloqueId"));

                Audiencia audiencia = new Audiencia()
                                .setFechaAudiencia(salaAudienciaRecord.fechaAudiencia())
                                .setSala(sala)
                                .setEstatusAudiencia(EstatusAudiencia.PROGRAMADA)
                                .setTipoAudiencia(tipoAudiencia)
                                .setCarpeta(carpeta)
                                .setBloque(bloque)
                                .setEstado(Estado.ACTIVE);

                return audienciaRepository.save(audiencia);
        }

        public ExtraAudienciaSelloRecord getAudienciaAndSalaAndDomicilio(Documento documento) {
                String nombreJuez = "";
                AudienciaOralidadFamiliarRecord audienciaOralidadFamiliarRcord = audienciaRepository
                                .getJuzAndSalaAndAudienciaByIdcarpeta(documento.getCarpeta().getId());

                String fechaFormateada = (audienciaOralidadFamiliarRcord != null)
                                ? audienciaOralidadFamiliarRcord.fechaAudiencia()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                : "";

                if (audienciaOralidadFamiliarRcord == null) {
                        audienciaOralidadFamiliarRcord = new AudienciaOralidadFamiliarRecord("", "", "", "", "",
                                        LocalDateTime.MIN);
                } else {
                        nombreJuez = audienciaOralidadFamiliarRcord.nombreJuez() + " "
                                        + audienciaOralidadFamiliarRcord.apellidoPaterno() + " "
                                        + (audienciaOralidadFamiliarRcord.apellidoMaterno() != null
                                                        ? audienciaOralidadFamiliarRcord.apellidoMaterno()
                                                        : "");
                }

                String calle = Optional.ofNullable(carpetaDetalleRepository.findByCarpetaId(documento.getCarpeta().getId()))
                                .map(detalle -> detalle.getUltimoDomicilioFamiliar() != null
                                                ? detalle.getUltimoDomicilioFamiliar()
                                                : detalle.getDomicilioFamiliar())
                                .orElse("");

                Etiqueta etiqueta = etiquetaRepository.findByTipoJuicioIdAndNombre(
                                documento.getCarpeta().getTipoJuicio().getId(), "domicilioOralidadFamiliar");
                String label = (etiqueta != null) ? etiqueta.getValue() + ":<b> " + calle + "</b>" : "";

                return new ExtraAudienciaSelloRecord(
                                nombreJuez,
                                audienciaOralidadFamiliarRcord.nombreSala(),
                                audienciaOralidadFamiliarRcord.nombreTipoJuicio(),
                                fechaFormateada,
                                label);
        }

        public Page<AudienciasGeneralesResponseRecord> getAllAudienciasGenerales(String key, Pageable pageable) {
                key = (key != null) ? key.toLowerCase() : "";
                Persona persona = personaService.getAuditor();
                Juzgado juzgado = persona.getJuzgado();
                List<RoleRecord> rolesPrincipales = personaService.getRolesByUser(persona.getUsuario());

                boolean esSecretario = rolesPrincipales.stream()
                                .map(RoleRecord::name) 
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .map(String::toUpperCase)
                                .anyMatch(r -> r.equals("SECRETARIO"));

                List<Integer> salaIdsPermitidas = List.of();

                if (esSecretario) {
                        salaIdsPermitidas = salaPersonaRepository.findSalaIdsByPersonaIdAndEstado(
                                        persona.getId().intValue(),
                                        Estado.ACTIVE);

                        if (salaIdsPermitidas.isEmpty()) {
                                return Page.empty(pageable);
                        }
                }

                return audienciaRepository.findAudienciasGenerales(pageable, juzgado, key, salaIdsPermitidas, esSecretario);
        }

        public void deleteAudiencia(Integer id) {
                try {
                        Audiencia audiencia = audienciaRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(AUDIENCIA_NOT_FOUND));
                        audiencia.setEstado(Estado.DELETED);
                        audienciaRepository.save(audiencia);
                } catch (DataIntegrityViolationException ex) {
                        throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "" + id);
                }
        }

        public List<CarpetaCatalogoRecord> getAudienciasMotivos() {
                return Arrays.stream(CatalogoMotivosRetrasoAudiencias.values())
                                .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                                .toList();
        }

        public void diferirAudiencia(Integer id) {
                try {
                        Audiencia audiencia = audienciaRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(AUDIENCIA_NOT_FOUND));

                        audiencia.setFechaAudiencia(null);
                        audiencia.setEstatusAudiencia(EstatusAudiencia.DIFERIDA);
                        audienciaRepository.save(audiencia);
                } catch (DataIntegrityViolationException ex) {
                        throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "audienciaId" + id);
                }
        }

        public AudienciasResponseRecord createAudiencia(AudienciaSaveRecord audiencia) {

                // TODO: AGREGAR VALIDACIÓN DE LA AGENDA ANTES DE EJECUTAR EL GUARDADO

                // Obtenemos sala:
                Sala sala = salaRepository.findById(audiencia.salaId())
                                .orElseThrow(() -> new NotFoundException(SALA_NOT_FOUND, "" + audiencia.salaId()));

                // obtenemos el tipo de audiencias:
                TipoAudiencia tipoAudiencia = tipoAudienciaRepository.findById(audiencia.tipoAudiencia())
                                .orElseThrow(() -> new NotFoundException(TIPO_AUDIENCIA_NOT_FOUND,
                                                "" + audiencia.tipoAudiencia()));

                // Obtenemos carpeta:
                Carpeta carpeta = carpetaRepository.findById(audiencia.carpetaId())
                                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));

                // Transformacion de fecha hora para empatar con el tipo de dato de la entidad
                LocalDateTime fechaHora = LocalDateTime.of(audiencia.fecha(), audiencia.hora());

                Audiencia audienciaNew = new Audiencia()
                                .setFechaAudiencia(fechaHora)
                                .setSala(sala)
                                .setInicio(fechaHora)
                                .setFin(fechaHora.plusMinutes(audiencia.duracion()))
                                .setEstatusAudiencia(EstatusAudiencia.PROGRAMADA)
                                .setTipoAudiencia(tipoAudiencia)
                                .setCarpeta(carpeta)
                                .setEstado(Estado.ACTIVE)
                                .setDescripcion(audiencia.descripcion());

                audienciaRepository.save(audienciaNew);

                return new AudienciasResponseRecord(audienciaNew.getId(), EstatusAudiencia.PROGRAMADA);
        }

        public List<String> getEstatusAudiencias() {
                return Arrays.stream(EstatusAudiencia.values())
                                .map(Enum::name)
                                .toList();
        }

        public void setHoraAudiencias(Integer id, LocalDateTime hora, boolean isInicio) {
                try {
                        Audiencia audiencia = audienciaRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(AUDIENCIA_NOT_FOUND));

                        if (isInicio) {
                                if (audiencia.getInicio() != null) {
                                        throw new ConflictException("La audiencia ya tiene una hora de inicio");
                                }
                                audiencia.setInicio(hora);
                                audienciaRepository.save(audiencia);
                        } else {
                                if (audiencia.getFin() != null) {
                                        throw new ConflictException("La audiencia ya tiene una hora de fin");
                                }
                                audiencia.setFin(hora);
                                audienciaRepository.save(audiencia);
                        }

                } catch (DataIntegrityViolationException ex) {
                        throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "audienciaId" + id);
                }
        }

        public void audienciaTabGeneral(AudienciaTabGeneralRecord audienciaTab) {

                Audiencia audiencia = audienciaRepository.findById(audienciaTab.idAudiencia())
                                .orElseThrow(() -> new NotFoundException(AUDIENCIA_NOT_FOUND, "AudienciaId"));

                TipoAudiencia tipoAudiencia = tipoAudienciaRepository.findById(audienciaTab.idTipoAudiencia())
                                .orElseThrow(() -> new NotFoundException(TIPO_AUDIENCIA_NOT_FOUND, "tipoAudienciaId"));

                Sala sala = salaRepository.findById(audienciaTab.idSala())
                                .orElseThrow(() -> new NotFoundException(SALA_NOT_FOUND, "SalaId"));

                audiencia.setTipoAudiencia(tipoAudiencia);
                audiencia.setSala(sala);
                audiencia.setEstatusAudiencia(audienciaTab.estatusAudiencia());

                if (audienciaTab.motivoRetraso() != null) {
                        audiencia.setMotivoRetrasoAudiencias(audienciaTab.motivoRetraso());
                }

                if (audienciaTab.resultadoDesahogo() != null) {
                        audiencia.setResultadosDesahogo(audienciaTab.resultadoDesahogo());
                }

                if (audienciaTab.actores() == null) {
                        audiencia.setAsisteActor(Asistencia.SI);
                        audiencia.setAsisteDemandano(Asistencia.SI);
                } else {
                        if (Objects.equals(audienciaTab.actores(), "ACTOR")) {
                                audiencia.setAsisteActor(Asistencia.NO);
                        } else if (Objects.equals(audienciaTab.actores(), "DEMANDADO")) {
                                audiencia.setAsisteDemandano(Asistencia.NO);
                        } else if (Objects.equals(audienciaTab.actores(), "AMBOS")) {
                                audiencia.setAsisteActor(Asistencia.NO);
                                audiencia.setAsisteDemandano(Asistencia.NO);
                        }
                }
                audienciaRepository.save(audiencia);
        }

        public AudienciasResponseRecord reprogramarAudiencia(ReprogramarAudienciaRecord audiencia) {
                Audiencia audienciaReprogramar = audienciaRepository.findById(audiencia.audienciaId())
                                .orElseThrow(() -> new NotFoundException(AUDIENCIA_NOT_FOUND, "AudienciaId"));

                Sala sala = salaRepository.findById(audiencia.salaId())
                                .orElseThrow(() -> new NotFoundException(SALA_NOT_FOUND, "SalaId"));

                TipoAudiencia tipoAudiencia = tipoAudienciaRepository.findById(audiencia.tipoAudiencia())
                                .orElseThrow(() -> new NotFoundException(TIPO_AUDIENCIA_NOT_FOUND, "tipoAudienciaId"));

                Carpeta carpeta = carpetaRepository.findById(audiencia.carpetaId())
                                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));

                LocalDateTime fechaHora = LocalDateTime.of(audiencia.fecha(), audiencia.hora());

                audienciaReprogramar.setFechaAudiencia(fechaHora);
                audienciaReprogramar.setSala(sala);
                audienciaReprogramar.setInicio(fechaHora);
                audienciaReprogramar.setFin(fechaHora.plusMinutes(audiencia.duracion()));
                audienciaReprogramar.setEstatusAudiencia(EstatusAudiencia.DIFERIDA);
                audienciaReprogramar.setTipoAudiencia(tipoAudiencia);
                audienciaReprogramar.setCarpeta(carpeta);
                audienciaReprogramar.setEstado(Estado.ACTIVE);
                audienciaReprogramar.setDescripcion(audiencia.descripcion());

                audienciaRepository.save(audienciaReprogramar);

                return new AudienciasResponseRecord(audienciaReprogramar.getId(), EstatusAudiencia.DIFERIDA);
        }

        public List<AudienciaAgendaRecord> getAgendaSala(Integer salaId) {
                return audienciaRepository.findBySalaIdAndFechaAudiencia(salaId, new Date());
        }

        public boolean validarDisponibilidad(ValidarDisponibilidadRequestRecord request) {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                .appendPattern("yyyy-MM-dd'T'HH:mm")
                                .optionalStart()
                                .appendPattern(":ss")
                                .optionalEnd()
                                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                                .toFormatter();

                String fechaHoraStr = request.fecha() + "T" + request.hora();
                LocalDateTime fechaInicio = LocalDateTime.parse(fechaHoraStr, formatter);

                LocalDateTime fechaFin = fechaInicio.plusMinutes(request.duracion());

                return !audienciaRepository.existeConflicto(
                                request.salaId(),
                                fechaInicio,
                                fechaFin);
        }
        public void guardarArchivo(MultipartFile file, Integer audienciaId) {
                digitalizacionService.guardarActaMinimaAudiencia(file, audienciaId);
        }

        public byte[] getAudienciaDocumento(Integer audienciaId) throws IOException {
                return digitalizacionService.getActaMinimaAudiencia(audienciaId);
        }

        public Audiencia obtenerUltimaAudienciaDesahogada() {
                Audiencia audiencia = audienciaRepository
                                .findFirstByEstatusAudienciaOrderByFechaAudienciaDesc(EstatusAudiencia.DESAHOGADA);
                if (audiencia == null) {
                        throw new EntityNotFoundException("No se encontró una audiencia desahogada.");
                }
                return audiencia;
        }

        public List<AudienciaProgramadaRecord> getAudienciasProgramadas(Integer carpetaId) {
                return audienciaRepository.findProgramadasByCarpetaId(carpetaId);
        }

        public List<AsistenciaPersonaDocumento> getParticipantesAudiencia(Integer carpetaId, Integer audienciaId){
                return audienciaRepository.findParticipantesByAudienciaId(carpetaId, audienciaId);
        }

}



