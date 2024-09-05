package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.util.Estado;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final JuzgadoRepository JuzgadoRepository;
    private final BloqueRepository BloqueRepository;

    @Transactional(readOnly = true)
    public Page<SalaRecord> getAll(Sala example, Pageable pageable ){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
        .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        
        Page<Sala> page = salaRepository.findAll(Example.of(example, exampleMatcher), pageable);
        
        List<SalaRecord> list = page.getContent().stream()
                .map(sala -> new SalaRecord(
                        sala.getId(),
                        sala.getNombre(),
                        sala.getJuez(),
                        sala.getJuzgado(), 
                        sala.getBloque()))
                .toList();



        return new PageImpl<>(list, pageable, page.getTotalElements());

    }

    @Transactional(readOnly = true)
    public SalaRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return salaRepository.findByIdAndEstadoIn(id, estados)
            .orElseThrow(() -> new NotFoundException("Sala no encontrada", "salaId"));
    }

    public SalaRecord create(Sala sala) {
        sala = salaRepository.save(sala);
        return new SalaRecord(sala.getId(), sala.getNombre(), sala.getJuez(), sala.getJuzgado(), sala.getBloque());
    }

    public SalaRecord update(Sala sala) {
        try {
            sala = salaRepository.save(sala);
            return new SalaRecord(sala.getId(), sala.getNombre(), sala.getJuez(), sala.getJuzgado(), sala.getBloque());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new OptimisticLockingFailureException("Sala modificada por otro usuario", "salaId");
        }
    }



}
