package mx.gob.pjpuebla.trials.core.bloques;

import java.util.Arrays;
import java.util.List;



import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    
    public List<Bloque> getAll2() {
        return bloqueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<BloqueRecord> getAll(Bloque example, Pageable pageable){
        Page<Bloque> page = bloqueRepository.findAll(Example.of(example), pageable);
        
        List<BloqueRecord> list = page.getContent().stream()
                .map(bloque -> new BloqueRecord(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal()))
                .toList();
        
        return new PageImpl<>(list, pageable, page.getTotalElements());
    
    }

}
