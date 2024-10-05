package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
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


}
