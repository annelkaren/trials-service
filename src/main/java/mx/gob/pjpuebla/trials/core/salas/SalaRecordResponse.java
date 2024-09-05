package mx.gob.pjpuebla.trials.core.salas;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecordResponse;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordResponse;
import mx.gob.pjpuebla.trials.util.Estado;

// se coloca momentaneamente juez como integer ya que no se tiene definida la asociación.
public record SalaRecordResponse(
    Integer id,
    String  nombre,
    Estado estado,
    Integer juez,
    BloqueRecordResponse bloque,
    JuzgadoRecordResponse juzgado
) {
    
}
