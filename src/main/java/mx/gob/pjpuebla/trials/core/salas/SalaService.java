package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final BloqueRepository bloqueRepository;
    private final PersonaRepository juezRepository;

    @Transactional(readOnly = true)
    public Page<SalaRecord> getAll(Sala example, Pageable pageable) {

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Sala> page = salaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<SalaRecord> list = page.getContent().stream()
                .map(sala -> new SalaRecord(
                        sala.getId(),
                        sala.getNombre(),
                        sala.getJuez().getNombre() + " " + sala.getJuez().getApellidoPaterno() + " "
                                + sala.getJuez().getApellidoMaterno(),
                        sala.getJuzgado().getNombre(),
                        new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(sala.getBloque().getId(),
                                sala.getBloque().getHoraInicial(), sala.getBloque().getHoraFinal()),
                        sala.getEstado()))

                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());

    }

    @Transactional(readOnly = true)
    public SalaRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return salaRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Sala no encontrada", "salaId"));
    }

    public Integer create(Sala sala) {

        sala.setJuez(juezRepository.findById(sala.getJuez().getId()).orElse(null));
        sala.setBloque(bloqueRepository.findById(sala.getBloque().getId()).orElse(null));
        sala.setJuzgado(juzgadoRepository.findById(sala.getJuzgado().getId()).orElse(null));
        sala.setNombre(getNameOfSala(sala));

        sala = salaRepository.save(sala);
        return sala.getId();
    }

    public Integer update(Sala sala) {
        try {

            sala.setJuez(juezRepository.findById(sala.getJuez().getId()).orElse(null));
            sala.setBloque(bloqueRepository.findById(sala.getBloque().getId()).orElse(null));
            sala.setJuzgado(juzgadoRepository.findById(sala.getJuzgado().getId()).orElse(null));

            sala = salaRepository.save(sala);
            return sala.getId();

        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Sala.class.getSimpleName());
        }
    }

    public String getNameOfSala(Sala sala) {
        if (sala.getJuzgado() != null && sala.getJuzgado().getId() != null) {
            int juzgadoId = sala.getJuzgado().getId();

            long count = salaRepository.countByJuzgadoId(juzgadoId);

            return String.valueOf(count + 1);
        } else {

            return "1";
        }
    }

}
