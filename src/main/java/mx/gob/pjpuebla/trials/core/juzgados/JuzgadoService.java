package mx.gob.pjpuebla.trials.core.juzgados;

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
public class JuzgadoService {

    private final JuzgadoRepository juzgadoRepository;
    private static final Logger LOG = LoggerFactory.getLogger(JuzgadoService.class);

    public Response getAll(Pageable pageable) {
        Response response = new Response();
        try {
            PagedModel<Juzgado> paginator = new PagedModel<>(this.juzgadoRepository.findAll(pageable));
            response.setData(paginator);
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener juzgados.");
        }
        return response;
    }

    public Response findById(Integer id) {
        Response response = new Response();
        try {
            response.setData(juzgadoRepository.findById(id).orElse(null));
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex) {
            LOG.error("findById ", ex);
            response.setMessage("Excepción. Error al obtener el juzgado con uuid: " + id);
        }
        return response;
    }

    public Response create(Juzgado juzgado) {
        Response response = new Response();
        try {
            juzgado = this.juzgadoRepository.save(juzgado);
            response.setMessage("El juzgado fue guardado con el id: " + juzgado.getId());
        } catch (Exception ex) {
            LOG.error("create ", ex);
            response.setMessage("Excepción. Error al guardar el registro.");
        }
        return response;
    }

    public Response update(Juzgado juzgado) {
        Response response = new Response();
        try {
            this.juzgadoRepository.save(juzgado);
            response.setMessage("Juzgado actualizado con id: " + juzgado.getId());
        } catch (OptimisticLockingFailureException ex) {
            LOG.error("update", ex);
            response.setMessage("El registro fue actualizado o eliminado por otra transaccion");
        } catch (Exception ex) {
            LOG.error("update", ex);
            response.setMessage("Error al actualizar el registro.");
        }
        return response;
    }

    public Response delete(Integer id) {
        Response response = new Response();
        try {
            this.juzgadoRepository.deleteById(id);
            response.setMessage("Juzgado eliminado con id: " + id);
        } catch (Exception ex) {
            LOG.error("delete ", ex);
            response.setMessage("Excepción. Error al eliminar el juzgado.");
        }
        return response;
    }
}