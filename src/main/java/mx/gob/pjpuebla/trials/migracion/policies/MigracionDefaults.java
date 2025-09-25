package mx.gob.pjpuebla.trials.migracion.policies;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;

/**
 * Defaults centralizados para altas bajo política MIGRATION.
 */
@Component
@RequiredArgsConstructor
public class MigracionDefaults {

    // ---- Carpeta ----
    public SelloEstatus defaultSelloCarpeta() { return SelloEstatus.VALIDO; }
    public EstadoCarpeta defaultEstadoCarpeta() { return EstadoCarpeta.MIGRADO; }
    public Migrado defaultFlagMigrado() { return Migrado.SI; }
    public LocalDateTime now() { return LocalDateTime.now(); }

    // ---- Concepto ----
    public Estado defaultEstadoConcepto() { return Estado.INACTIVE; }

}