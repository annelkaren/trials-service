package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasResponseRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AsistenciaAudienciaService {

    private final AsistenciaAudienciaRepository asistenciaAudienciaRepository;

    public Page<AsistenciaAudienciaResponse> getAll(Pageable pageable) {

        Page<AsistenciaAudiencia> page = asistenciaAudienciaRepository.findAll(pageable);

        List<AsistenciaAudienciaResponse> responses = page.getContent().stream()
                .map(asistenciaAudiencia -> new AsistenciaAudienciaResponse(
                        asistenciaAudiencia.getId(),
                        asistenciaAudiencia.getPersonaDocumento().getId(),
                        new AudienciasResponseRecord(asistenciaAudiencia.getAudiencia().getId(), asistenciaAudiencia.getAudiencia().getEstatusAudiencia()),
                        asistenciaAudiencia.getAsistencia(),
                        asistenciaAudiencia.getDocumentoIdentificacion().getName(),
                        asistenciaAudiencia.getUrlDocumento()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

}
