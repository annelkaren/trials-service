package mx.gob.pjpuebla.trials.core.conceptos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<ConceptoRecordResponse> getAll(Pageable pageable) {
        Page<Concepto> page = conceptoRepository.findAll(pageable);
        List<ConceptoRecordResponse> list = page.getContent().stream()
                .map(this::mapToRecordResponse)
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ConceptoRecordResponse findById(Integer id) {
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Concepto no encontrado", "conceptoId"));
        return mapToRecordResponse(concepto);
    }

    private ConceptoRecordResponse mapToRecordResponse(Concepto concepto) {
        Juzgado juzgado = concepto.getJuzgado();
        JuzgadoRecordItem juzgadoRecordItem = null;

        if (juzgado != null) {
            String materiaNombre = (juzgado.getMateria() != null) ? juzgado.getMateria().getNombre() : null;
            juzgadoRecordItem = new JuzgadoRecordItem(
                    juzgado.getId(),
                    juzgado.getNombre(),
                    juzgado.getEstado(),
                    materiaNombre
            );
        }

        return new ConceptoRecordResponse(
                concepto.getId(),
                concepto.getNombre(),
                concepto.getDias(),
                concepto.getTipoConcepto(),
                juzgadoRecordItem,
                concepto.getEstado()
        );
    }
}
