package mx.gob.pjpuebla.trials.core.documentoidentificacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoIdentificacionService {

    private final DocumentoIdentificacionRepository documentoIdentificacionRepository;

    @Transactional(readOnly = true)
    public List<IdentificacionDocRecord> getAll() {
        return documentoIdentificacionRepository.findAll().stream()
                .map(identificacion -> new IdentificacionDocRecord(identificacion.getId(), identificacion.getName()))
                .toList();
    }
}
