package mx.gob.pjpuebla.migracion.readers.exhortoCapital;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para consultar exhortos capitalinos por campo "juzgadoOr".
 */
@Service
@RequiredArgsConstructor
public class ExhortosCapitalMigracionReader {

    private final ExhortosCapitalMigracionRepository repository;

    /**
     * Retorna una lista de exhortos capitalinos asociados a un juzgado origen.
     *
     * @param juzgadoOr Clave del juzgado origen
     * @return Lista de registros encontrados
     */
    public List<ExhortosCapitalMigracion> buscarPorJuzgadoOr(String juzgadoOr) {
        return repository.findByJuzgadoOr(juzgadoOr);
    }
}