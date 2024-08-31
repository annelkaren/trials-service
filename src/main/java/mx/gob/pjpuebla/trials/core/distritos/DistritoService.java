package mx.gob.pjpuebla.trials.core.distritos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistritoService {

    private final DistritoRepository distritoRepository;

    @Transactional(readOnly = true)
    public Page<DistritoRecord> getAllActive(Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Distrito> page = distritoRepository.findAll(Example.of(new Distrito().setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<DistritoRecord> list = page.getContent().stream()
                .map(m -> new DistritoRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
