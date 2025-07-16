package mx.gob.pjpuebla.migracion.oficios;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OficiosMigracionService {

    private final OficiosMigracionRepository oficiosMigracionRepository;

    /**
     * Busca todos los oficios relacionados con un CU específico.
     *
     * @param cu Clave única de la entrada
     * @return Lista de oficios encontrados
     */
    public List<OficiosMigracion> buscarPorCu(String cu) {
        return oficiosMigracionRepository.findByCu(cu);
    }
}