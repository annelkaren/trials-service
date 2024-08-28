package mx.gob.pjpuebla.trials.core.tiposistema;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipoSistemaService {

    private final TipoSistemaRepository tipoSistemaRepository;

    @Transactional(readOnly = true)
    public List<TipoSistemaRecord> getAll(Pageable pageable, TipoSistema example){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Example<TipoSistema> exampleQuery = Example.of(example.setEstado("A"), exampleMatcher);
        Page<TipoSistema> page = tipoSistemaRepository.findAll(exampleQuery, pageable);

        return page.getContent().stream()
                .map(m -> new TipoSistemaRecord(m.getId(), m.getNombre()))
                .toList();
    }
}
