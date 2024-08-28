package mx.gob.pjpuebla.trials.core.organismos;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
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
public class OrganismoService {

    private final OrganismoRepository organismoRepository;

    @Transactional(readOnly = true)
    public List<OrganismoRecord> getAll(Pageable pageable, Organismo example){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Example<Organismo> exampleQuery = Example.of(example.setEstado("A"), exampleMatcher);
        Page<Organismo> page = organismoRepository.findAll(exampleQuery, pageable);

        if (page.isEmpty()) {
            throw new NotFoundException("Organismos no encontrados", "");
        }

        return page.getContent().stream()
                .map(m -> new OrganismoRecord(m.getId(), m.getNombre()) )
                .toList();
    }
}
