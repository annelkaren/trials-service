package mx.gob.pjpuebla.trials.core.bloques;

import java.util.Arrays;
import java.util.List;

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
    
    public List<Bloque> findAll() {
        return bloqueRepository.findAll();
    }

}
