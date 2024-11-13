package mx.gob.pjpuebla.trials.workflow.identificacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class IdentificacionService {

    private final IdentificacionRepository identificacionRepository;

    @Transactional(readOnly = true)
    public List<IdentificacionDocRecord> getAll() {
        return identificacionRepository.findAll().stream()
                .map(identificacion -> new IdentificacionDocRecord(identificacion.getId(), identificacion.getName()))
                .toList();
    }
}
