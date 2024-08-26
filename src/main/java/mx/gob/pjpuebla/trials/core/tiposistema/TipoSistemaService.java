package mx.gob.pjpuebla.trials.core.tiposistema;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TipoSistemaService {

    private final TipoSistemaRepository tipoSistemaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TipoSistemaService.class);

    public List<TipoSistema> getAll(Pageable pageable){
        try {
            return this.tipoSistemaRepository.findAll(pageable).getContent();
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            throw new RuntimeException("Error al obtener Tipo Sistema", ex);
        }

    }
}
