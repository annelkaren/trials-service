package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TipoJuicioEtiquetaService {

    private final TipoJuicioEtiquetaRepository tipoJuicioEtiquetaRepository;

    public List<TipoJuicioEtiquetaItem> getAllByTipoJuicioId(Integer id) {
        List<TipoJuicioEtiqueta> originalList = this.tipoJuicioEtiquetaRepository.findByTipoJuicioId(id);
        return originalList.stream()
                .map(tje -> new TipoJuicioEtiquetaItem(tje.getNombre(), tje.getValue()))
                .toList();
    }
}
