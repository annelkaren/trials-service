package mx.gob.pjpuebla.migracion.readers.oficios;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OficiosMigracionReader {

    private final OficiosMigracionRepository oficiosMigracionRepository;

    /**
     * Busca todos los oficios relacionados con un CU específico.
     *
     * @param cu Clave única de la entrada
     * @return Lista de oficios encontrados
     */
    public List<OficiosMigracion> buscarPorCu(String cu) {
        return oficiosMigracionRepository.findByCuAndEstatusOfiIn(cu, List.of("S", "N"));
    }
}