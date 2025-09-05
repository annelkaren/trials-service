package mx.gob.pjpuebla.migracion.readers.exhortoForaneo;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para consultar exhortos foráneos por clave "exhorto".
 */
@Service
@RequiredArgsConstructor
public class ExhortoForaneoMigracionReader {
      private final ExhortoForaneoMigracionRepository exhortoForaneoMigracionRepository;

    /**
     * Busca todos los registros de exhortos foráneos relacionados con un exhorto.
     *
     * @param exhorto Clave del exhorto
     * @return Lista de exhortos encontrados
     */
    public List<ExhortoForaneoMigracion> buscarPorJuzgadoOr(String jugadoOr) {
        return exhortoForaneoMigracionRepository.findByJuzgadoOr(jugadoOr);
    }
}
