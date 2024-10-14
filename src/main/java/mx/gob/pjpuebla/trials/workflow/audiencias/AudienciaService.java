package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.etiquetas.Etiqueta;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaRepository;
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

@Transactional
@RequiredArgsConstructor
@Service
public class AudienciaService {
    private final AudienciaRepository audienciaRepository;
    private final SalaRepository salaRepository;
    private final BloqueRepository bloqueRepository;
    private final EtiquetaRepository etiquetaRepository;

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
        AudienciaOralidadFamiliarRecord audienciaOralidadFamiliarRcord =
                audienciaRepository.getJuzAndSalaAndAudienciaByIdcarpeta(documento.getCarpeta().getId());

        String fechaFormateada = (audienciaOralidadFamiliarRcord != null) ?
                audienciaOralidadFamiliarRcord.fechaAudiencia().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";

        if (audienciaOralidadFamiliarRcord == null) {
            audienciaOralidadFamiliarRcord = new AudienciaOralidadFamiliarRecord("", "", "", LocalDateTime.MIN);
        }

        String calle = documento.getData().getDomicilio();
        Etiqueta etiqueta = etiquetaRepository.findByTipoJuicioIdAndNombre(
                documento.getCarpeta().getTipoJuicio().getId(), "domicilioOralidadFamiliar");
        String label = (etiqueta != null) ? etiqueta.getValue() + ":<b> " + calle + "</b>" : "";

        return new ExtraAudienciaSelloRecord(
                audienciaOralidadFamiliarRcord.nombreJuez(),
                audienciaOralidadFamiliarRcord.nombreSala(),
                audienciaOralidadFamiliarRcord.nombreTipoJuicio(),
                fechaFormateada,
                label
        );
    }
}
