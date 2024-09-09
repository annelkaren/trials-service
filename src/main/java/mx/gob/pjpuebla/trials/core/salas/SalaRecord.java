package mx.gob.pjpuebla.trials.core.salas;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record SalaRecord(
    Integer id,
    String nombre,
    String juez,
    String juzgado,
    String horario,
    Estado estado) {
}
