package mx.gob.pjpuebla.trials.core.bloques;

import java.util.Arrays;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.Estado;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BloqueService {
    private final BloqueRepository bloqueRepository;

    public BloqueRecord findById(Integer id){
        Bloque bloque = bloqueRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
            .orElseThrow(() -> new NotFoundException("Bloque no encontrado", "bloqueId"));
        return new BloqueRecord(id, bloque.getHoraInicial(), bloque.getHoraFinal());
    }   

    public BloqueRecord create(Bloque bloque){
        bloqueRepository.save(bloque);
        return new BloqueRecord(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal());
    }

    public BloqueRecord update(Bloque bloque){
        try {
            bloqueRepository.save(bloque);
            return new BloqueRecord(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal());
        } catch (OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException(
                    "Bloque modificado por otro usuario", "bloqueId");
        }
    }
}
