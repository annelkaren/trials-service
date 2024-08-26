package mx.gob.pjpuebla.trials.core.tipooficialias;

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
public class TipoOficialiaService {

    private final TipoOficialiasRepository tipoOficialiaRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TipoOficialiaService.class);

    public List<TipoOficialias> getAll(Pageable pageable){
        try {
            return this.tipoOficialiaRepository.findAll(pageable).getContent();
        } catch (Exception ex) {
            LOG.error("getAll ", ex);
            throw new RuntimeException("Error al obtener Tipo Oficialias", ex);
        }
    }
}
