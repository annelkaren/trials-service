package mx.gob.pjpuebla.trials.migracion.policies;

/** Define la política bajo la cual se crean/actualizan entidades. */
public enum CreationPolicy {
    DEFAULT,   // flujo normal de la app
    MIGRATION  // reglas especiales para migración (estados, flags, validaciones)
}