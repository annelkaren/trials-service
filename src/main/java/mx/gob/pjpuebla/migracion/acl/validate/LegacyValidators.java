package mx.gob.pjpuebla.migracion.acl.validate;

import org.springframework.stereotype.Component;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;

/** Validadores de precondiciones para datos provenientes de legacy. */
@Component
public class LegacyValidators {

    public void requireNonNullJuzgado(Juzgado juzgado) {
        if (juzgado == null) {
            throw new IllegalArgumentException("El 'juzgado' no puede ser nulo.");
        }
    }

    public void requireNonEmpty(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " no puede ser nulo ni vacío.");
        }
    }
}