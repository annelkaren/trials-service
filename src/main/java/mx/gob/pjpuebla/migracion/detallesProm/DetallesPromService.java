package mx.gob.pjpuebla.migracion.detallesProm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para consultar detalles de promociones por CU.
 */
@Service
@RequiredArgsConstructor
public class DetallesPromService {

    private final DetallesPromRepository detallesPromRepository;

    /**
     * Busca todos los detalles de promoción asociados a una CU.
     *
     * @param cu Identificador CU
     * @return Lista de detalles encontrados
     */
    public List<DetallesProm> buscarPorCu(String cu) {
        return detallesPromRepository.findByCu(cu);
    }
}