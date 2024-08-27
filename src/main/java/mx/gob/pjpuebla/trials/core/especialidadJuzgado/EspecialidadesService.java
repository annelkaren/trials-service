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
public class EspecialidadesService {
    private final EspecialidadesRepository especialidadesRepository;

    @Transactional(readOnly = true)
    public Page<EspecialidadesRecord> getAllActive(Pageable pageable, Especialidades example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Especialidades> page = especialidadesRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<EspecialidadesRecord> list = page.getContent().stream()
                .map(m -> new EspecialidadesRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public EspecialidadesRecord findById(Integer id) {
        Especialidades especialidades = especialidadesRepository.findByIdAndEstado(id, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Especialidad de Juzgado no encontrada", "especialidadId"));
        return new EspecialidadesRecord(especialidades.getId(), especialidades.getNombre());
    }

}
