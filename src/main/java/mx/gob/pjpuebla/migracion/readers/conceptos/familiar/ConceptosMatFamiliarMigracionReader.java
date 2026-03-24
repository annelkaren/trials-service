package mx.gob.pjpuebla.migracion.readers.conceptos.familiar;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConceptosMatFamiliarMigracionReader {
    
    private final ConceptosMatFamiliarMigracionRepository conceptosMatFamiliarMigracionRepository;

    public Optional<ConceptosMatFamiliarMigracion> findConceptoMatFamiliarByClave(String clave){
        return conceptosMatFamiliarMigracionRepository.findByClave(clave);
    }


    /** Busca un concepto por clave (case-insensitive). */
    public Optional<ConceptosMatFamiliarMigracion> findByClave(String clave) {
        String k = normalize(clave);
        return conceptosMatFamiliarMigracionRepository.findByClave(k);
    }

    /** Helper: devuelve los días parseados o 0 si no existe/malformado. */
    public int getDiasByClaveOrZero(String clave) {
        return findByClave(clave)
                .map(c -> parseDias(c.getDias()))
                .orElse(0);
    }

    private static String normalize(String s) { return s == null ? null : s.trim(); }

    private static int parseDias(String s) {
        if (s == null) return 0;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return 0; }
    }
}
