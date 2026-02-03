package mx.gob.pjpuebla.trials.core.salas;

import java.util.List;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record SalaRecordSave(
    Integer salaId,
    Integer bloqueId,
    Estado estado,
    Integer juezId,
    Integer juzgadoId,
    List<Long> secretarios

) {}