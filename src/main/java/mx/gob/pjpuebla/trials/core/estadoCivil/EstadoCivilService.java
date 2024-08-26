package mx.gob.pjpuebla.trials.core.estadoCivil;

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
public class EstadoCivilService {
    private final EstadoCivilRepository estadoCivilRepository;
    private static final Logger LOG = LoggerFactory.getLogger(EstadoCivilService.class);

    public List<EstadoCivil> getAll(Pageable pageable){
        try {
            return this.estadoCivilRepository.findAll(pageable).getContent();
        } catch (Exception ex){
            LOG.error("getAll ", ex);
            throw new RuntimeException("Error al obtener Estado Civil", ex);
        }
    }
}
