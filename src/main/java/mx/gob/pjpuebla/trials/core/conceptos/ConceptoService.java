package mx.gob.pjpuebla.trials.core.conceptos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ConceptoService {
    private final ConceptoRepository conceptoRepository;

    @Transactional(readOnly = true)
    public List<ConceptoRecordResponse> getAll(Integer tipoJuicioId) {
      return conceptoRepository.findAllByTipoJuicio(tipoJuicioId).stream()
                .map(concepto -> new ConceptoRecordResponse(
                    concepto.getId(),
                    concepto.getNombre(),
                    concepto.getDias(),
                    concepto.getEstado()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConceptoRecordResponse findById(Integer id) {
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Concepto no encontrado", "conceptoId"));
        return new ConceptoRecordResponse(concepto.getId(), concepto.getNombre(), concepto.getDias(), concepto.getEstado());
    }

}
