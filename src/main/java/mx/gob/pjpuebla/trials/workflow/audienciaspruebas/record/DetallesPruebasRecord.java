package mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record;

import mx.gob.pjpuebla.trials.util.enums.DesistimientoAdmision;

public record DetallesPruebasRecord(
        Integer idAudienciaPruebas,
        String asistente,
        String tipoPrueba,
        String descripcion,
        String tipoPerito,
        DesistimientoAdmision desistimientoAdmision
) {
}
