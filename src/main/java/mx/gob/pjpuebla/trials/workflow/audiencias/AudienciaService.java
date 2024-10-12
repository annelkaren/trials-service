package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas.TipoJuicioEtiquetaRepository;
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
    private final TipoJuicioEtiquetaRepository tipoJuicioEtiquetaRepository;

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
        String tipoOralidadFamiliar = "";
        String fechaFormateada;
        String etiqueta;
        AudienciaOralidadFamiliarRecord audienciaOralidadFamiliarRcord =
                audienciaRepository.getJuzAndSalaAndAudienciaByIdcarpeta(documento.getCarpeta().getId());

        if (audienciaOralidadFamiliarRcord == null) {
            audienciaOralidadFamiliarRcord = new AudienciaOralidadFamiliarRecord("", "", "", LocalDateTime.MIN);
            fechaFormateada = "";
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            fechaFormateada = audienciaOralidadFamiliarRcord.fechaAudiencia().format(formatter);
        }

        //Domicilio Familiar Sello
        if (documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("alimentos")){
            tipoOralidadFamiliar = "eOralidadFamiliarA";
        }else if(documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("divorcio") &&
                documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("incausado") &&
                documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("unilateral"))
        {
            tipoOralidadFamiliar = "eOralidadFamiliarDIU";
        }else if (documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("divorcio") &&
                documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("incausado") &&
                documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("bilateral"))
        {
            tipoOralidadFamiliar = "eOralidadFamiliarDIB";
        } else if (documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("guardia") &&
                documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("custodia"))
        {
            tipoOralidadFamiliar = "eOralidadFamiliarGC";
        } else if (documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("visita ") &&
                documento.getCarpeta().getTipoJuicio().getNombre().toLowerCase().contains("convivencia"))
        {
            tipoOralidadFamiliar = "eOralidadFamiliarVC";
        }else{
            tipoOralidadFamiliar = "XX";
        }

        String calle = documento.getData().getDomicilio();
        etiqueta = tipoJuicioEtiquetaRepository.getEtiquetaByNombreAndTipoJuicio(documento.getCarpeta().getTipoJuicio().getId(), tipoOralidadFamiliar);

        if(etiqueta == null){
            etiqueta = "";
        }else{
            etiqueta = etiqueta + ":<b> " + calle + "</b>";
        }

        return new ExtraAudienciaSelloRecord(
                audienciaOralidadFamiliarRcord.nombreJuez(),
                audienciaOralidadFamiliarRcord.nombreSala(),
                audienciaOralidadFamiliarRcord.nombreTipoJuicio(),
                fechaFormateada,
                etiqueta
        );
    }



}
