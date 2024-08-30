package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PersonaService.class);
    private final DomicilioService domicilioService;

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        Persona persona = personaRepository.findById(id)
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
            LOG.error("update OptimisticLockingFailureException ", ex);
            throw new OptimisticLockingFailureException(ex.getMessage());
        }
    }
}