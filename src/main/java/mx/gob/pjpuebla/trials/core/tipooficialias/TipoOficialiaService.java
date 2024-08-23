package mx.gob.pjpuebla.trials.core.tipooficialias;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TipoOficialiaService {

    private final TipoOficialiasRepository tipoOficialiaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TipoOficialiaService.class);

    public Response getAll(Pageable pageable){
        Response response = new Response();
        try {
            PagedModel<TipoOficialias> paginator = new PagedModel<>(this.tipoOficialiaRepository.findAll(pageable));
            response.setData(paginator);
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener Tipo Oficialia.");
        }
        return response;
    }
}
