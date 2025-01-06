package mx.gob.pjpuebla.trials.core.conceptos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ConceptoService {
    private final ConceptoRepository conceptoRepository;
    private final CarpetaRepository carpetaRepository;

    @Transactional(readOnly = true)
    public List<ConceptoRecordResponse> getAll(Integer carpetaId) {
      Carpeta carpeta = carpetaRepository.findById(carpetaId).orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "CarpetaId"+carpetaId));

      return conceptoRepository.findAllByTipoJuicio_IdOrNombreIn(carpeta.getTipoJuicio().getId(), List.of("Adjuntar", "Distribución")).stream()
                .map(concepto -> new ConceptoRecordResponse(
                    concepto.getId(),
                    concepto.getNombre().toUpperCase(),
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
