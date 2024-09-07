package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.dao.OptimisticLockingFailureException;
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
    public PersonaRecord findById(Long id) {
        Persona persona = personaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "personaId"));
        return new PersonaRecord(persona.getId(), persona.getNombre(), persona.getApellidoPaterno(), persona.getApellidoMaterno(), persona.getPseudonimo());
    }

    @Transactional(readOnly = true)
    public List<PersonaRecord> findAllJueces(){
        return personaRepository.findAllJueces();
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