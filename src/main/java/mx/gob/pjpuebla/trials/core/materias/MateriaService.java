package mx.gob.pjpuebla.trials.core.materias;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


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
                .map(m -> new MateriaRecord(m.getId(), StringUtils.capitalize(m.getNombre().toLowerCase())))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MateriaRecord findById(Integer id) {
        Materia materia = materiaRepository.findByIdAndEstado(id, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId"));
        return new MateriaRecord(materia.getId(), StringUtils.capitalize(materia.getNombre().toLowerCase()));
    }

    @Transactional(readOnly = true)
    public List<MateriaRecord> findMateriasPublicas() {
        List<Materia> materias = materiaRepository.findByNombreNotInOrderByNombre(Collections.singletonList("EXHORTO"));

        return materias.stream()
                .map(m -> new MateriaRecord(m.getId(), StringUtils.capitalize(m.getNombre().toLowerCase())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SentenciasByMateriaRecord> getCountSentenciasByMaterias() {
        return materiaRepository.getCountSentenciasByMateria();
    }
}
