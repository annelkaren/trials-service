package mx.gob.pjpuebla.migracion.readers.actores;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActoresMigracionReader {

    private final ActoresMigracionRepository actoresMigracionRepository;

    /**
     * Busca todos los actores asociados a una clave específica.
     *
     * @param clave Clave de búsqueda
     * @return Lista de actores encontrados
     */
    public List<ActoresMigracion> buscarPorClave(String clave) {
        return actoresMigracionRepository.findByClave(clave);
    }
}