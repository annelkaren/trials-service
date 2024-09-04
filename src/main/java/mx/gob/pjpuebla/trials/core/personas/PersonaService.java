package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final DomicilioService domicilioService;

    @Transactional(readOnly = true)
    public Page<PersonaRecordResponse> getAll(Persona example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Persona> page = personaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<PersonaRecordResponse> list = page.getContent().stream()
                .map(persona ->
                        new PersonaRecordResponse(persona.getId(),
                                persona.getNombre() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno(),
                                persona.getCorreoElectronico(),
                                persona.getCelular()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        Persona persona = personaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "personaId"));
        return new PersonaRecord(persona.getId(), persona.getNombre(), persona.getApellidoPaterno(), persona.getApellidoMaterno(), persona.getPseudonimo());
    }

    public PersonaRecord create(Persona persona) {
        persona.setDomicilio(domicilioService.save(persona.getDomicilio()));
        persona = personaRepository.save(persona);
        return new PersonaRecord(persona.getId(), persona.getNombre(), persona.getApellidoPaterno(), persona.getApellidoMaterno(), persona.getPseudonimo());
    }

    public PersonaRecord update(Persona persona) {
        try {
            persona.setDomicilio(domicilioService.save(persona.getDomicilio()));
            personaRepository.save(persona);
            return new PersonaRecord(persona.getId(), persona.getNombre(), persona.getApellidoPaterno(), persona.getApellidoMaterno(), persona.getPseudonimo());
        } catch (OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException("Persona modificada por otro usuario", "personaId");
        }
    }
}