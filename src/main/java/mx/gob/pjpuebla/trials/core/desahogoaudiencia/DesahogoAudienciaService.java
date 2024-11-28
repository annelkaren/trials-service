package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DesahogoAudienciaService {

    private final DesahogoAudienciaRepository desahogoAudienciaRepository;

    @Transactional(readOnly = true)
    public List<DesahogoAudienciaRecord> getAll() {
        List<DesahogoAudiencia> desahogoAudienciaList = desahogoAudienciaRepository.findAll();
        return desahogoAudienciaList.stream()
                .map(d -> new DesahogoAudienciaRecord(d.getId(), d.getKey(), d.getNombre()))
                .toList();
    }
}