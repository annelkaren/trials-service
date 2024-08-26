package mx.gob.pjpuebla.trials.core.organismos;


import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganismoService {
    private final OrganismoRepository organismoRepository;
    private static final Logger LOG = LoggerFactory.getLogger(OrganismoService.class);

    public List<Organismo> getAll(Pageable pageable){
        try {
            return this.organismoRepository.findAll(pageable).getContent();
        }catch (Exception ex) {
            LOG.error("getAll ", ex);
            throw new RuntimeException("Error al obtener Organismos", ex);
        }

    }
}
