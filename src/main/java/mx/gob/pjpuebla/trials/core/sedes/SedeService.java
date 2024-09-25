package mx.gob.pjpuebla.trials.core.sedes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
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
public class SedeService {

    private final SedeRepository sedeRepository;
    private final DistritoRepository distritoRepository;
    private final DomicilioService domicilioService;

    @Transactional(readOnly = true)
    public Page<SedeRecordResponse> getAll(Sede example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Sede> page = sedeRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<SedeRecordResponse> list = page.getContent().stream()
                .map(sede -> new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public SedeRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return sedeRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
    }

    public SedeRecordResponse create(Sede sede) {
        sede.setDistrito(distritoRepository.findById(sede.getDistrito().getId()).orElse(null));
        sede.setDomicilio(domicilioService.save(sede.getDomicilio()));
        sede = sedeRepository.save(sede);
        return new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado());
    }

    public SedeRecordResponse update(Sede sede) {
        try {
            sede.setDistrito(distritoRepository.findById(sede.getDistrito().getId()).orElse(null));
            sede.setDomicilio(domicilioService.save(sede.getDomicilio()));
            sedeRepository.save(sede);
            return new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Sede.class.getSimpleName());
        }
    }

    @Transactional(readOnly = true)
    public Page<SedeDomiciliosRecord> getAllSedesAndDomicilios(Pageable pageable) {
        return sedeRepository.findSedesDomiciliosByJuzgadoId(pageable);
    }


    public void delete(Integer id) {
        sedeRepository.deleteById(id);
    }

}
