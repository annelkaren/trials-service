package mx.gob.pjpuebla.migracion.readers.exhortoCapital;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para consultar exhortos capitalinos por campo "juzgado".
 */
@Service
@RequiredArgsConstructor
public class ExhortosCapitalMigracionReader {

    private final ExhortosCapitalMigracionRepository repository;

    /**
     * Retorna una lista de exhortos capitalinos asociados a un juzgado origen.
     *
     * @param juzgado Clave del juzgado origen
     * @return Lista de registros encontrados
     */
    public List<ExhortosCapitalMigracion> buscarPorJuzgado(String juzgado) {
        return repository.findByJuzgado(juzgado);
    }

    public List<ExhortosCapitalMigracion> buscarPorExpAmoJuzgado(String expediente, Integer amo, String juzgado){
        return  repository.findByNumeroAndAmoAndJuzgado(expediente, amo, juzgado);
    }
}