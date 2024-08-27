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
public class PersonaService {

    private final PersonaRepository personaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PersonaService.class);

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "personaId"));
        return new PersonaRecord(persona.getId(), persona.getNombre());
    }

    public Response create(Persona persona) {
        Response response = new Response();
        try {
            persona = this.personaRepository.save(persona);
            response.setMessage("El registro fue guardado con el UUID: " + persona.getId());
        } catch (Exception ex) {
            LOG.error("create", ex);
            response.setMessage("Error al guardar el registro.");
        }
        return response;
    }

    public Response update(Persona persona) {
        Response response = new Response();
        try {
            this.personaRepository.save(persona);
            response.setMessage("Registro actualizado.");
        } catch (OptimisticLockingFailureException ex) {
            LOG.error("update", ex);
            response.setMessage("El registro fue actualizado o eliminado por otra transaccion");
        } catch (Exception ex) {
            LOG.error("update", ex);
            response.setMessage("Error al actualizar el registro.");
        }
        return response;
    }
}