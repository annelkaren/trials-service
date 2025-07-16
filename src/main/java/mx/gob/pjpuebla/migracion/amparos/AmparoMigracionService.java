package mx.gob.pjpuebla.migracion.amparos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmparoMigracionService {

    private final AmparoMigracionRepository amparoMigracionRepository;

    /**
     * Busca un amparo por CU.
     *
     * @param cu Clave única de la entrada
     * @return el objeto Amparo si existe, de lo contrario null
     */
    public List<AmparosMigracion> buscarPorCu(String cu) {
        return amparoMigracionRepository.findByCu(cu);
    }
}