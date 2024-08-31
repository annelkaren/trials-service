package mx.gob.pjpuebla.trials.core.tipopartes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPartesService {

    private final TipoPartesRepository tipoPartesRepository;

    public Page<TipoPartesRecord> getAll(Pageable pageable, TipoPartes example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoPartes> page = tipoPartesRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<TipoPartesRecord> list = page.getContent().stream()
                .map(m -> new TipoPartesRecord(m.getId(), m.getNombre(), m.getMateria().getId()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TipoPartesRecord findById(Integer id) {
        TipoPartes tipoPartes = tipoPartesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TipoPartes no encontrada", "id"));
        return new TipoPartesRecord(tipoPartes.getId(), tipoPartes.getNombre(), tipoPartes.getMateria().getId());
    }

    public List<TipoPartesRecord> findByMateriaId(Integer materiaId) {
        List<TipoPartes> tipoPartes = tipoPartesRepository.findByMateriaId(materiaId);
        return tipoPartes.stream().map(entity -> new TipoPartesRecord(entity.getId(), entity.getNombre(), entity.getMateria().getId())).toList();
    }
}