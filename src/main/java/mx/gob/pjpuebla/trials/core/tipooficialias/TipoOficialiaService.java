package mx.gob.pjpuebla.trials.core.tipooficialias;

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
public class TipoOficialiaService {

    private final TipoOficialiasRepository tipoOficialiaRepository;

    @Transactional(readOnly = true)
    public List<TipoOficialiaRecord> getAll(Pageable pageable, TipoOficialias example){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Example<TipoOficialias> exampleQuery = Example.of(example.setEstado("A"), exampleMatcher);
        Page<TipoOficialias> page = tipoOficialiaRepository.findAll(exampleQuery, pageable);

        return page.getContent().stream()
                .map(m -> new TipoOficialiaRecord(m.getId(), m.getNombre()))
                .toList();
    }
}
