package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.util.enums.CatalogoMotivosRetrasoAudiencias;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaSaveRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaTabGeneralRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public class AudienciaSetUp {
    
    public AudienciaSetUp(){

    }


    public static Audiencia generarAudiencia(LocalDateTime fechaAudiencia, Sala sala, Bloque bloque, TipoAudiencia tipoAudiencia, Carpeta carpeta){
        return new Audiencia()
        .setFechaAudiencia(fechaAudiencia)
        .setSala(sala)
        .setBloque(bloque)
        .setTipoAudiencia(tipoAudiencia)
        .setCarpeta(carpeta)
        .setEstatusAudiencia(EstatusAudiencia.PROGRAMADA)
        .setEstado(Estado.ACTIVE);
    }

    public static AudienciaSaveRecord audienciaSaveRecordCreate(){
        return new AudienciaSaveRecord(
            1,
            1,
            1, 
            LocalDate.now(), 
            LocalTime.now(), 
            12, 
            "descripcion");
    }


    public static AudienciasGeneralesResponseRecord createAudienciasGeneralesResponseRecord() {
        return new AudienciasGeneralesResponseRecord(1, "Sentencia del incidente", "Juez 1", "000001/2024", "Sala 1", LocalDateTime.now(), EstatusAudiencia.PROGRAMADA);
    }

    public static AudienciaTabGeneralRecord createAudienciaTabGeneralRecord() {
        return new AudienciaTabGeneralRecord(1,1,1, CatalogoMotivosRetrasoAudiencias.RETRASO_AUDIENCIA, "Otro", "Ambos", EstatusAudiencia.PROGRAMADA);
    }
}
