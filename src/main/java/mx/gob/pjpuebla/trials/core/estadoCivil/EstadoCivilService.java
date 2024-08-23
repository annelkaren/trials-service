package mx.gob.pjpuebla.trials.core.estadoCivil;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EstadoCivilService {
    private final EstadoCivilRepository estadoCivilRepository;
    private static final Logger LOG = LoggerFactory.getLogger(EstadoCivilService.class);

    public Response getAll(Pageable pageable){
        Response response = new Response();
        try {
            PagedModel<EstadoCivil> paginator = new PagedModel<>(this.estadoCivilRepository.findAll(pageable));
            response.setData(paginator);
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex){
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener Tipo Sistema.");
        }
        return response;
    }
}
