package mx.gob.pjpuebla.trials.core.materias;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;


@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(MateriaService.class);

    public Response getAll(Pageable pageable) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(materiaRepository.findAll(pageable));
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener materias.");
        }
        return response;
    }

    public Response findById(Integer id) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(materiaRepository.findById(id).orElse(null));
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener la materia" + id);
        }
        return response;
    }

    public Response create(Materia materia, BindingResult bindingResult) {
        Response response = new Response();
        MateriaValidator validator = new MateriaValidator();
        try {
            validator.validate(materia, bindingResult);
            if (bindingResult.hasErrors()) {
                response.setMessage("Error al guardar el registro por validacion: " + bindingResult.toString() + " Materia: " + materia.getId());
            } else {
                this.materiaRepository.save(materia);
                response.setMessage("La materia (" + materia.getId() + ") fue guardada.");
            }
        } catch (Exception ex) {
            LOG.error("create ", ex);
            response.setMessage("Excepción. Error al guardar el registro Materia: " + materia.getId());
        }
        return response;
    }

    public Response update(Materia materia) {
        Response response = new Response();
        try {
            materia = this.materiaRepository.save(materia);
            response.setMessage("Materia actualizada. (" + materia.getId() + ")");

        } catch (OptimisticLockingFailureException ex) {
            LOG.error("update", ex);
            response.setMessage("El registro fue actualizado o eliminado por otra transaccion");
        } catch (Exception ex) {
            LOG.error("update ", ex);
            response.setMessage("Excepción. Error al actualizar Materia: " + materia.getId());
        }
        return response;
    }

    public Response delete(Integer id) {
        Response response = new Response();
        try {
            this.materiaRepository.deleteById(id);
            response.setMessage("Materia eliminada. (" + id + ")");
        } catch (Exception ex) {
            LOG.error("delete ", ex);
            response.setMessage("Excepción. Error al eliminar la Materia: " + id);
        }
        return response;
    }
}
