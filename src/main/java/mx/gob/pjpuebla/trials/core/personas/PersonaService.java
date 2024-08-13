package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PersonaService.class);

    public Response getAll(Pageable pageable) {
        Response response = new Response();
        try {
            PagedModel<Persona> paginator = new PagedModel<>(this.personaRepository.findAll(pageable));
            response.setData(paginator);
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex) {
            LOG.error("getAll", ex);
            response.setMessage("Error al realizar la petición.");
        }
        return response;
    }

    public Response findById(Long id) {
        Response response = new Response();
        try {
            response.setData(this.personaRepository.findById(id).orElse(null));
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex) {
            LOG.error("findById", ex);
            response.setMessage("Error al obtener el registro con id " + id);
        }
        return response;
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

    public Response delete(Long id) {
        Response response = new Response();
        try {
            this.personaRepository.deleteById(id);
            response.setMessage("Registro eliminado.");
        } catch (Exception ex) {
            LOG.error("delete", ex);
            response.setMessage("Error al eliminar el registro.");
        }
        return response;
    }
}
