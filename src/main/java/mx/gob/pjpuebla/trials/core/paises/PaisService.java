package mx.gob.pjpuebla.trials.core.paises;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PaisService {

    private final PaisRepository paisRepository;

    @Transactional(readOnly = true)
    public List<PaisRecord> getAll() {
        List<Pais> paisList = paisRepository.findAll();
        return paisList.stream()
                .map(p -> new PaisRecord(p.getNombreComun(), p.getKey(), p.getId().toString()))
                .sorted(Comparator.comparing(PaisRecord::nombre)).toList();
    }

}
