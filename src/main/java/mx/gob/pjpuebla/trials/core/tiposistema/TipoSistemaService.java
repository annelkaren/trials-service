package mx.gob.pjpuebla.trials.core.tiposistema;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TipoSistemaService {

    private final TipoSistemaRepository tipoSistemaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TipoSistemaService.class);

    public Response getAll(Pageable pageable){
        Response response = new Response();
        try {
            PagedModel<TipoSistema> paginator = new PagedModel<>(this.tipoSistemaRepository.findAll(pageable));
            response.setData(paginator);
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener Tipo Sistema.");
        }

        return response;
    }
}
