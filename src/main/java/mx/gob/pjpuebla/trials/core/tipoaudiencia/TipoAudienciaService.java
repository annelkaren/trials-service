package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class TipoAudienciaService {
    private final TipoAudienciaRepository tipoAudienciaRepository;

    public TipoAudiencia obtenerTipoAudiencia(String tipoAudiencia) {
        return tipoAudienciaRepository.findByNombre(tipoAudiencia);
    }
}
