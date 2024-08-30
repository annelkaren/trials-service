package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
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
    private final DomicilioRepository domicilioRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PersonaService.class);
    private final DomicilioService domicilioService;

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "personaId"));
        return new PersonaRecord(persona.getId(), persona.getNombre(), persona.getApellidoPaterno(), persona.getApellidoMaterno(), persona.getPseudonimo());
    }

    public Persona create(Persona persona) {

        personaRepository.save(persona);
        return persona;
    }

    public Persona update(Persona persona) {
        try {
            Domicilio domicilio = new Domicilio();


            Domicilio domt = domicilioRepository.save(domicilio);

            persona.setDomicilio(domt);
            personaRepository.save(persona);
        } catch (OptimisticLockingFailureException ex) {
            LOG.error("update OptimisticLockingFailureException ", ex);
        } catch (Exception ex) {
            LOG.error("update", ex);
        }
        return persona;
    }
}