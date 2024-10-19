package mx.gob.pjpuebla.trials.core.estadocivil;


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
public class EstadoCivilService {

    private final EstadoCivilRepository estadoCivilRepository;

    @Transactional(readOnly = true)
    public List<EstadoCivilRecord> getAll(Pageable pageable, EstadoCivil example){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Example<EstadoCivil> exampleQuery = Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher);
        Page<EstadoCivil> page = estadoCivilRepository.findAll(exampleQuery, pageable);

        return page.getContent().stream()
                .map(m -> new EstadoCivilRecord(m.getId(), m.getNombre()))
                .toList();
    }
}
