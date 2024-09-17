package mx.gob.pjpuebla.trials.core.oficialias;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OficialiaService {

    private final OficialiaRepository oficialiaRepository;
    private final SedeRepository sedeRepository;
    private final TipoOficialiaRepository tipoOficialiaRepository;

    @Transactional(readOnly = true)
    public Page<OficialiaRecord> getAllActive(Pageable pageable, Oficialia example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Oficialia> page = oficialiaRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<OficialiaRecord> list = page.getContent().stream()
                .map(m -> new OficialiaRecord(m.getId(), m.getVersion(), m.getNombre(), m.getResponsable(), m.getEstado(),
                        new TipoOficialiaRecord(m.getTipoOficialia().getId(), m.getTipoOficialia().getNombre()),
                        new SedeRecordResponse(m.getSede().getId(), m.getSede().getNombre(), m.getSede().getEstado())))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public OficialiaRecord findById(Integer id) {
        return oficialiaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException("Oficialia no encontrada", "oficialiaId: " + id));
    }

    public OficialiaRecordResponse create(Oficialia oficialia) {
        oficialia.setSede(sedeRepository.findById(oficialia.getSede().getId())
                .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
        oficialia.setTipoOficialia(tipoOficialiaRepository.findById(oficialia.getTipoOficialia().getId())
                .orElseThrow(() -> new NotFoundException("Tipo oficialia no encontrada", "tipoOficialiaId")));
        oficialia = oficialiaRepository.save(oficialia);
        return new OficialiaRecordResponse(oficialia.getId(), oficialia.getNombre());
    }

    public OficialiaRecordResponse update(Oficialia oficialia) {
        try {
            oficialia.setSede(sedeRepository.findById(oficialia.getSede().getId())
                    .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
            oficialia.setTipoOficialia(tipoOficialiaRepository.findById(oficialia.getTipoOficialia().getId())
                    .orElseThrow(() -> new NotFoundException("Tipo oficialia no encontrada", "tipoOficialiaId")));
            oficialia = oficialiaRepository.save(oficialia);
            return new OficialiaRecordResponse(oficialia.getId(), oficialia.getNombre());
        } catch (OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException("Oficialia modificada por otro usuario", "oficialiaId: " + oficialia.getId());
        }
    }

    public void delete(Integer id) {
        oficialiaRepository.deleteById(id);
    }

}