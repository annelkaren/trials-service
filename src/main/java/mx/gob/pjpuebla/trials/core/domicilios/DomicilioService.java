package mx.gob.pjpuebla.trials.core.domicilios;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@RequiredArgsConstructor
@Service
public class DomicilioService {

    private final DomicilioRepository domicilioRepository;
    private static final Logger LOG = LoggerFactory.getLogger(DomicilioService.class);

    public Response findById(Long id) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(domicilioRepository.findById(id).orElse(null));
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener el domicilio");
        }
        return response;
    }

    public Response create(Domicilio domicilio, BindingResult bindingResult) {
        Response response = new Response();
        DomicilioValidator validator = new DomicilioValidator();
        try {
            validator.validate(domicilio, bindingResult);
            if (bindingResult.hasErrors()) {
                response.setMessage("Error al guardar el registro por validacion: " + bindingResult.toString());
            } else {
                this.domicilioRepository.save(domicilio);
                response.setMessage("El domicilio fue guardado con el UUID: " + domicilio.getId());
            }
        } catch (Exception ex) {
            LOG.error("create ", ex);
            response.setMessage("Excepción. Error al guardar el registro.");
        }
        return response;
    }

    public Response update(Domicilio domicilio) {
        Response response = new Response();
        try {
            this.domicilioRepository.save(domicilio);
            response.setMessage("Registro Actualizado");
        } catch (OptimisticLockingFailureException ex) {
            LOG.error("update ", ex);
            response.setMessage("El registro fue actualizado o eliminado por otra transacccion");
        } catch (Exception ex) {
            LOG.error("update ", ex);
            response.setMessage("Excepción. Error al actualizar Domicilio.");
        }
        return response;
    }

    public Response delete(Long id) {
        Response response = new Response();
        try {
            this.domicilioRepository.deleteById(id);
            response.setMessage("Domicilio eliminado.");
        } catch (Exception ex) {
            LOG.error("delete ", ex);
            response.setMessage("Excepción. Error al eliminar el domicilio.");
        }
        return response;
    }
}