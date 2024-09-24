package mx.gob.pjpuebla.trials.core.bloques;

import java.util.List;

import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BloqueService {
    private final BloqueRepository bloqueRepository;

    @Transactional(readOnly = true)
    public Page<BloqueRecord> getAll(Bloque example, Pageable pageable) {

        if (example.getHoraInicial() != null) {

            return bloqueRepository.findByHoraInicial(example.getHoraInicial(), pageable)
                    .map(bloque -> new BloqueRecord(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal(), bloque.getEstado()));
        } else {

            Page<Bloque> page = bloqueRepository.findAll(pageable);
            List<BloqueRecord> list = page.getContent().stream()
                    .map(bloque -> new BloqueRecord(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal(), bloque.getEstado()))
                    .toList();

            return new PageImpl<>(list, pageable, page.getTotalElements());
        }
    }

    public BloqueRecordResponse create(Bloque bloque) {
        bloque = bloqueRepository.save(bloque);
        return new BloqueRecordResponse(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal(), bloque.getEstado());
    }

    public BloqueRecordResponse update(Bloque bloque) {
        try {
            bloqueRepository.save(bloque);
            return new BloqueRecordResponse(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal(), bloque.getEstado());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Bloque.class.getSimpleName());
        }
    }

    public void delete(Integer id) {
        bloqueRepository.deleteById(id);
    }

}
