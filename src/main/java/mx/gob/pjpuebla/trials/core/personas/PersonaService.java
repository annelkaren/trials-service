package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Estado;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PersonaService.class);

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "personaId"));
        return new PersonaRecord(persona.getId(), persona.getNombre(), persona.getApellidoPaterno(), persona.getApellidoMaterno(), persona.getPseudonimo());
    }

    public Persona create(Persona persona) {
        persona = this.personaRepository.save(persona);
        return persona;
    }

    public Persona update(Persona persona) {
        try {
            this.personaRepository.save(persona);
        } catch (OptimisticLockingFailureException ex) {
            LOG.error("update OptimisticLockingFailureException ", ex);
        } catch (Exception ex) {
            LOG.error("update", ex);
        }
        return persona;
    }
}