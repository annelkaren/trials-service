package mx.gob.pjpuebla.trials.core.tipoJuicio;

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
public class TipoJuicioService {

    private final TipoJuicioRepository tipoJuicioRepository;

    @Transactional(readOnly = true)
    public Page<TipoJuicioRecord> getAllActive(Pageable pageable, TipoJuicio example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoJuicio> page = tipoJuicioRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<TipoJuicioRecord> list = page.getContent().stream()
                .map(m -> new TipoJuicioRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TipoJuicioRecord findById(Integer id) {
        TipoJuicio tipoJuicio = tipoJuicioRepository.findByIdAndEstado(id, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Tipo de Juicio no encontrado", "tipoJuicioId"));
        return new TipoJuicioRecord(tipoJuicio.getId(), tipoJuicio.getNombre());
    }

}
