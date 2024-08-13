package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class TipoPartesService {

    @Autowired
    private TipoPartesRepository tipoPartesRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TipoPartesService.class);

    public Response getAll(Pageable pageable) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(tipoPartesRepository.findAll(pageable)); // tipo de dato: Page<Juzgado>
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener juzgados.");
        }
        return response;
    }

    public Response findById(Integer id) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(tipoPartesRepository.findById(id).orElse(null));  // tipo de dato: Juzgado
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener el juzgado");
        }
        return response;
    }

    public Response create(TipoPartes tipoPartes, BindingResult bindingResult) {
        Response response = new Response();
        TipoPartesValidator validator = new TipoPartesValidator();
        try {
            validator.validate(tipoPartes, bindingResult);
            if (bindingResult.hasErrors()) {
                response.setMessage("Error al guardar el registro por validacion: " + bindingResult.toString());
            } else {
                this.tipoPartesRepository.save(tipoPartes);
                response.setMessage("Tipo Parte fue guardado con el UUID: " + tipoPartes.getId());
            }
        } catch (Exception ex) {
            LOG.error("create ", ex);
            response.setMessage("Excepción. Error al guardar el registro.");
        }
        return response;
    }

    public Response update(TipoPartes tipoPartes) {
        Response response = new Response();
        try {
            this.tipoPartesRepository.save(tipoPartes);
            response.setMessage("Tipo Parte actualizado.");
        } catch (Exception ex) {
            LOG.error("update ", ex);
            response.setMessage("Excepción. Error al actualizar Juzgado.");
        }
        return response;
    }

    public Response delete(Integer id) {
        Response response = new Response();
        try {
            this.tipoPartesRepository.deleteById(id);
            response.setMessage("Tipo Parte eliminado.");
        } catch (Exception ex) {
            LOG.error("delete ", ex);
            response.setMessage("Excepción. Error al eliminar el juzgado.");
        }
        return response;
    }

}
