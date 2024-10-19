package mx.gob.pjpuebla.trials.core.domicilios;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class DomicilioService {

    private final DomicilioRepository domicilioRepository;

    @Transactional(readOnly = true)
    public Domicilio findById(Long id) {
        return domicilioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Domicilio no encontrado", "domicilioId"));
    }

    public Domicilio save(Domicilio domicilio) {
        return this.domicilioRepository.save(domicilio);
    }
}