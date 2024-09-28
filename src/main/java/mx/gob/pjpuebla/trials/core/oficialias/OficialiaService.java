package mx.gob.pjpuebla.trials.core.oficialias;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OficialiaService {

    private final OficialiaRepository oficialiaRepository;
    private final SedeRepository sedeRepository;
    private final TipoOficialiaRepository tipoOficialiaRepository;
    private final MateriaRepository materiaRepository;
    private final JuzgadoRepository juzgadoRepository;

    @Transactional(readOnly = true)
    public Page<OficialiaRecord> getAllActive(Pageable pageable, Oficialia example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Oficialia> page = oficialiaRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<OficialiaRecord> list = page.getContent().stream()
                .map(m -> new OficialiaRecord(m.getId(), m.getVersion(), m.getNombre(), m.getResponsable(), m.getEstado(),
                        new TipoOficialiaRecord(m.getTipoOficialia().getId(), m.getTipoOficialia().getNombre()),
                        new SedeRecordResponse(m.getSede().getId(), m.getSede().getNombre(), m.getSede().getEstado())))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public OficialiaRecord findById(Integer id) {
        return oficialiaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException("Oficialia no encontrada", "oficialiaId: " + id));
    }

    public OficialiaRecordResponse create(Oficialia oficialia) {

            if (oficialia.getSede() != null && oficialia.getSede().getId() != null) {
                Sede sede = sedeRepository.findById(oficialia.getSede().getId())
                        .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
                oficialia.setSede(sede);
            }

            if (oficialia.getTipoOficialia() != null && oficialia.getTipoOficialia().getId() != null) {
                TipoOficialia tipoOficialia = tipoOficialiaRepository.findById(oficialia.getTipoOficialia().getId())
                        .orElseThrow(() -> new NotFoundException("Tipo Oficialia no encontrada", "tipoOficialiaId"));
                oficialia.setTipoOficialia(tipoOficialia);
            }

            if (oficialia.getJuzgado() != null && oficialia.getJuzgado().getId() != null) {
                Juzgado juzgado = juzgadoRepository.findById(oficialia.getJuzgado().getId())
                        .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
                oficialia.setJuzgado(juzgado);
            } else {
                oficialia.setJuzgado(null);
            }

            if (oficialia.getMaterias() != null) {
                List<Integer> mIds = oficialia.getMaterias().stream()
                        .filter(materia -> materia.getId() != null)
                        .map(Materia::getId)
                        .toList();

                List<Materia> materias = materiaRepository.findAllById(mIds);
                oficialia.setMaterias(materias);
            }

            oficialia = oficialiaRepository.save(oficialia);
            return new OficialiaRecordResponse(oficialia.getId(), oficialia.getNombre());
    }

    public OficialiaRecordResponse update(Oficialia oficialia) {
        try {
            Oficialia existingOficialia = oficialiaRepository.findById(oficialia.getId())

                    .orElseThrow(() -> new NotFoundException("Oficialia no encontrada", "oficialiaId"));
            existingOficialia.setNombre(oficialia.getNombre());
            existingOficialia.setEstado(oficialia.getEstado());


            existingOficialia.setSede(sedeRepository.findById(oficialia.getSede().getId())
                    .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));

            existingOficialia.setTipoOficialia(tipoOficialiaRepository.findById(oficialia.getTipoOficialia().getId())
                    .orElseThrow(() -> new NotFoundException("Tipo Oficialia no encontrada", "tipoOficialiaId")));

            if (oficialia.getJuzgado() != null && oficialia.getJuzgado().getId() != null) {
            existingOficialia.setJuzgado(juzgadoRepository.findById(oficialia.getJuzgado().getId())
                    .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId")));
            } else {
                existingOficialia.setJuzgado(null);
            }


            if (oficialia.getMaterias() != null) {
                List<Integer> mIds = oficialia.getMaterias().stream()
                        .map(Materia::getId).toList();

                List<Materia> materias = materiaRepository.findAllById(mIds);
                existingOficialia.setMaterias(materias);
            }

            oficialiaRepository.save(existingOficialia);

            return new OficialiaRecordResponse(existingOficialia.getId(), existingOficialia.getNombre());
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Oficialia.class.getSimpleName());
        }
    }


    @Transactional(readOnly = true)
    public Page<OficialiaMateriaRecord> getAllByOficialiaMateria(Pageable pageable) {
        List<Estado> estados = Arrays.asList(Estado.ACTIVE, Estado.INACTIVE);
        return oficialiaRepository.findOficialiaDetails(estados, pageable);
    }

    public void delete(Integer id) {
        oficialiaRepository.deleteById(id);
    }

}