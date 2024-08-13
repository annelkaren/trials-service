package mx.gob.pjpuebla.trials.core.oficialias;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@RequiredArgsConstructor
@Service
public class OficialiaService {

    private final OficialiaRepository oficialiaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(OficialiaService.class);

    public Response getAll(Pageable pageable) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(oficialiaRepository.findAll(pageable)); // tipo de dato: Page<Juzgado>
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener Oficialia.");
        }
        return response;
    }

    public Response findById(Integer id) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(oficialiaRepository.findById(id).orElse(null));  // tipo de dato: Juzgado
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener Oficialia");
        }
        return response;
    }

    public Response create(Oficialia oficialia, BindingResult bindingResult) {
        Response response = new Response();
        OficialiaValidator validator = new OficialiaValidator();
        try {
            validator.validate(oficialia, bindingResult);
            if (bindingResult.hasErrors()) {
                response.setMessage("Error al guardar el registro por validacion: " + bindingResult.toString());
            } else {
                this.oficialiaRepository.save(oficialia);
                response.setMessage("Tipo Parte fue guardado con el UUID: " + oficialia.getId());
            }
        } catch (Exception ex) {
            LOG.error("create ", ex);
            response.setMessage("Excepción. Error al guardar el registro.");
        }
        return response;
    }

    public Response update(Oficialia oficialia) {
        Response response = new Response();
        try {
            this.oficialiaRepository.save(oficialia);
            response.setMessage("Oficializa actualizado.");
        } catch (Exception ex) {
            LOG.error("update ", ex);
            response.setMessage("Excepción. Error al actualizar Oficialia.");
        }
        return response;
    }

    public Response delete(Integer id) {
        Response response = new Response();
        try {
            this.oficialiaRepository.deleteById(id);
            response.setMessage("Oficialia eliminado.");
        } catch (Exception ex) {
            LOG.error("delete ", ex);
            response.setMessage("Excepción. Error al eliminar Oficialia.");
        }
        return response;
    }

}