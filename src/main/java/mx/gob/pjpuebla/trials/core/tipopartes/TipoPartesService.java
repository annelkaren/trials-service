package mx.gob.pjpuebla.trials.core.tipopartes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPartesService {

    private final TipoPartesRepository tipoPartesRepository;

    public Page<TipoPartesRecord> getAll(Pageable pageable, TipoPartes tipoPartes) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoPartes> page = tipoPartesRepository.findAll(pageable);
        List<TipoPartesRecord> list = page.getContent().stream()
                .map(m -> new TipoPartesRecord(m.getId(), m.getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public TipoPartesRecord findById(Integer id) {
        TipoPartes tipoPartes = tipoPartesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrada", "materiaId"));
        return new TipoPartesRecord(tipoPartes.getId(), tipoPartes.getNombre());
    }

    public TipoPartesRecord findByMateriaId(Integer materiaId) {
        TipoPartes tipoPartes = tipoPartesRepository.findByMateriaId(materiaId)
                .orElseThrow(() -> new NotFoundException("Tipo Partes no encontrada", "materiaId"));
        return new TipoPartesRecord(tipoPartes.getId(), tipoPartes.getNombre());
    }
}