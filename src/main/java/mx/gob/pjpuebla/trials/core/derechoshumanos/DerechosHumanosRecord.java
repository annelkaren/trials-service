package mx.gob.pjpuebla.trials.core.derechoshumanos;

import mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos;

public record DerechosHumanosRecord(
        Integer id,
        String  derechoHumano,
        TipoDerechosHumanos tipoDerechoHumano
) {
}
