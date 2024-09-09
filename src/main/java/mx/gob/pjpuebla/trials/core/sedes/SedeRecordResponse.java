package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.util.enums.Estado;

public record SedeRecordResponse(Integer id, String nombre, Estado estado) {
}
