package mx.gob.pjpuebla.trials.core.materias;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;

    @Transactional(readOnly = true)
    public Page<MateriaRecord> getAllActive(Pageable pageable, Materia example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Materia> page = materiaRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<MateriaRecord> list = page.getContent().stream()
                .map(m -> new MateriaRecord(m.getId(), m.getNombre()))
                .collect(Collectors.toUnmodifiableList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MateriaRecord findById(Integer id) {
        Materia materia = materiaRepository.findByIdAndEstado(id, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId"));
        return new MateriaRecord(materia.getId(), materia.getNombre());
    }

}
