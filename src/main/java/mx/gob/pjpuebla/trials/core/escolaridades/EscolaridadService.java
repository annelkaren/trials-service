package mx.gob.pjpuebla.trials.core.escolaridades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscolaridadService {

    private final EscolaridadRepository escolaridadRepository;

    @Transactional(readOnly = true)
    public Page<EscolaridadRecord> getAllActive(Pageable pageable, Escolaridad example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Escolaridad> page = escolaridadRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<EscolaridadRecord> list = page.getContent().stream()
                .map(m -> new EscolaridadRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
