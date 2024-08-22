package mx.gob.pjpuebla.trials.core.tipopartes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoPartesService {

    private final TipoPartesRepository tipoPartesRepository;

    public Response getAll(Pageable pageable) {
        Response response = new Response();
        try {
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
            response.setData(tipoPartesRepository.findAll(pageable)); // tipo de dato: Page<Juzgado>
        } catch (Exception ex) {
            log.error("getAll ", ex);
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
            log.error("getAll ", ex);
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
            log.error("create ", ex);
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
            log.error("update ", ex);
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
            log.error("delete ", ex);
            response.setMessage("Excepción. Error al eliminar el juzgado.");
        }
        return response;
    }

}
