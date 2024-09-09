package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JuzgadoService {

    private final JuzgadoRepository juzgadoRepository;
    private final SedeRepository sedeRepository;
    private final MateriaRepository materiaRepository;

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordResponse> getAll(Juzgado example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Juzgado> page = juzgadoRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<JuzgadoRecordResponse> list = page.getContent().stream()
                .map(juzgado -> new JuzgadoRecordResponse(
                        juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(),
                        juzgado.getMateria().getNombre()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public JuzgadoRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return juzgadoRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
    }

    public JuzgadoRecordResponse create(Juzgado juzgado) {
        juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElse(null));
        juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElse(null));
        juzgado = juzgadoRepository.save(juzgado);
        juzgadoRepository.generarSecuenciaExpediente(juzgado.getId());
        return new JuzgadoRecordResponse(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getMateria().getNombre());
    }

    public JuzgadoRecordResponse update(Juzgado juzgado) {
        try {
            juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElse(null));
            juzgado.setSede(sedeRepository.findById(juzgado.getId()).orElse(null));
            juzgado = juzgadoRepository.save(juzgado);
            return new JuzgadoRecordResponse(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getMateria().getNombre());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException("Juzgado modificado por otro usuario", "juzgadoId");
        }
    }

    public void delete(Integer id) {
        juzgadoRepository.deleteById(id);
        juzgadoRepository.eliminarSecuenciaExpediente(id);
    }

    public NumeroExpedienteResponse getNumeroExpediente(Integer id){
        return new NumeroExpedienteResponse(juzgadoRepository.getNumeroExpediente(id));
    }

    public Boolean reiniciarSecuenciasExpedientes(){
        return juzgadoRepository.reiniciarSecuenciasExpedientes();
    }
}