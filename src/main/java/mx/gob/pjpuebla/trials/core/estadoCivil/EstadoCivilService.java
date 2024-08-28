package mx.gob.pjpuebla.trials.core.estadoCivil;


import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EstadoCivilService {

    private final EstadoCivilRepository estadoCivilRepository;

    @Transactional(readOnly = true)
    public List<EstadoCivilRecord> getAll(Pageable pageable, EstadoCivil example){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Example<EstadoCivil> exampleQuery = Example.of(example.setEstado("A"), exampleMatcher);
        Page<EstadoCivil> page = estadoCivilRepository.findAll(exampleQuery, pageable);

        if (page.isEmpty()) {
            throw new NotFoundException("Estados Civiles no encontrados", "");
        }

        return page.getContent().stream()
                .map(m -> new EstadoCivilRecord(m.getId(), m.getNombre()))
                .toList();
    }
}
