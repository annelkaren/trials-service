package mx.gob.pjpuebla.migracion.acl.normalizer;

import org.springframework.stereotype.Component;

/** Normaliza valores de expediente (trim, validaciones básicas). */
@Component
public class ExpedienteNormalizer {
    public String normalizeExpediente(String expediente) {
        if (expediente == null || expediente.isBlank()) {
            throw new IllegalArgumentException("El 'expediente' no puede ser nulo ni vacío.");
        }
        return expediente.trim();
    }
}