package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.CatalogoMotivosRetrasoAudiencias;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.etiquetas.Etiqueta;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class AudienciaService {
    private final AudienciaRepository audienciaRepository;
    private final SalaRepository salaRepository;
    private final BloqueRepository bloqueRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final PersonaService personaService;

    public Audiencia create(SalaAudienciaRecord salaAudienciaRecord, TipoAudiencia tipoAudiencia, Carpeta carpeta) {
        Sala sala = salaRepository.findById(salaAudienciaRecord.id())
                .orElseThrow(() -> new NotFoundException("Sala no encontrada", "SalaId"));
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
        AudienciaOralidadFamiliarRecord audienciaOralidadFamiliarRcord =
                audienciaRepository.getJuzAndSalaAndAudienciaByIdcarpeta(documento.getCarpeta().getId());

        String fechaFormateada = (audienciaOralidadFamiliarRcord != null) ?
                audienciaOralidadFamiliarRcord.fechaAudiencia().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";

        if (audienciaOralidadFamiliarRcord == null) {
            audienciaOralidadFamiliarRcord = new AudienciaOralidadFamiliarRecord("", "", "", "", "", LocalDateTime.MIN);
        } else {
            nombreJuez = audienciaOralidadFamiliarRcord.nombreJuez() + " " + audienciaOralidadFamiliarRcord.apellidoPaterno() + " " + (audienciaOralidadFamiliarRcord.apellidoMaterno() != null ? audienciaOralidadFamiliarRcord.apellidoMaterno() : "");
        }

        String calle = Optional.of(documento)
                .map(Documento::getData)
                .map(DocumentoData::getDomicilio)
                .orElse("");

        Etiqueta etiqueta = etiquetaRepository.findByTipoJuicioIdAndNombre(
                documento.getCarpeta().getTipoJuicio().getId(), "domicilioOralidadFamiliar");
        String label = (etiqueta != null) ? etiqueta.getValue() + ":<b> " + calle + "</b>" : "";

        return new ExtraAudienciaSelloRecord(
                nombreJuez,
                audienciaOralidadFamiliarRcord.nombreSala(),
                audienciaOralidadFamiliarRcord.nombreTipoJuicio(),
                fechaFormateada,
                label
        );
    }

    public Page<AudienciasGeneralesResponseRecord> getAllAudienciasGenerales(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaService.getAuditor();
        Juzgado juzgado = persona.getJuzgado();

        Page<Audiencia> page = audienciaRepository.findByJuzgado(juzgado, key, pageable);

        List<AudienciasGeneralesResponseRecord> list = page.getContent().stream()
                .map(item -> {
                    String nombreCompleto = item.getSala().getJuez().getNombre() + " " + item.getSala().getJuez().getApellidoPaterno();

                    if (item.getSala().getJuez().getApellidoMaterno() != null) {
                        nombreCompleto += " " + item.getSala().getJuez().getApellidoMaterno();
                    }

                    return new AudienciasGeneralesResponseRecord(
                            item.getId(),
                            item.getTipoAudiencia().getNombre(),
                            nombreCompleto,
                            item.getCarpeta().getExpediente(),
                            item.getSala().getNombre(),
                            item.getFechaAudiencia(),
                            item.getEstatusAudiencia()
                    );
                })
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public void deleteAudiencia(Integer id) {
        try {
            Audiencia audiencia = audienciaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Audiencia no encontrada"));
            audiencia.setEstado(Estado.DELETED);
            audienciaRepository.save(audiencia);
        } catch (DataIntegrityViolationException ex) {
            throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "audienciaId" + id);
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
                    .orElseThrow(() -> new IllegalArgumentException("Audiencia no encontrada"));

            audiencia.setFechaAudiencia(null);
            audiencia.setEstatusAudiencia(EstatusAudiencia.DIFERIDA);
            audienciaRepository.save(audiencia);
        } catch (DataIntegrityViolationException ex) {
            throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "audienciaId" + id);
        }
    }
}
