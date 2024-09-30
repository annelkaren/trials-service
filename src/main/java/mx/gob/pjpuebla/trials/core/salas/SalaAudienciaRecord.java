package mx.gob.pjpuebla.trials.core.salas;
import java.time.LocalDateTime;

public record SalaAudienciaRecord (
    Integer id,
    String nombre,
    Long juezId,
    String juez,
    String juzgado,
    Integer bloqueId,
    LocalDateTime fechaAudiencia
){}