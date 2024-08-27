package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EspecialidadService {
    private final EspecialidadRepository especialidadRepository;

    @Transactional(readOnly = true)
    public Page<EspecialidadRecord> getAllActive(Pageable pageable, Especialidad example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Especialidad> page = especialidadRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<EspecialidadRecord> list = page.getContent().stream()
                .map(m -> new EspecialidadRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public EspecialidadRecord findById(Integer id) {
        Especialidad especialidad = especialidadRepository.findByIdAndEstado(id, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Especialidad de Juzgado no encontrada", "especialidadId"));
        return new EspecialidadRecord(especialidad.getId(), especialidad.getNombre());
    }

}
