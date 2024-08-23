package mx.gob.pjpuebla.trials.core.organismos;


import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

@RequiredArgsConstructor
@Service
public class OrganismoService {

    private final OrganismoRepository organismoRepository;
    private static final Logger LOG = LoggerFactory.getLogger(OrganismoService.class);
    public Response getAll(Pageable pageable){
        Response response = new Response();
        try {
            PagedModel<Organismo> paginator = new PagedModel<>(this.organismoRepository.findAll(pageable));
            response.setData(paginator);
            response.setMessage("La solicitud se ha completado satisfactoriamente.");
        }catch (Exception ex) {
            LOG.error("getAll ", ex);
            response.setMessage("Excepción. Error al obtener Organismo.");
        }
        return response;
    }
}
