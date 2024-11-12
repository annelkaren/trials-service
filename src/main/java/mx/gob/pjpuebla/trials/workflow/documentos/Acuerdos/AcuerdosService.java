package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdosRecord;
@RequiredArgsConstructor
@Service
public class AcuerdosService {

    private final DocumentoRepository documentoRepository;

    public Page<AcuerdosRecord> getAcuerdos(String key, Integer carpetaId, Pageable pageable){
        key = (key != null) ? key.toLowerCase() : "";
        Page<AcuerdosRecord> page = documentoRepository.findAllAcuerdosByCarpeta(key, carpetaId, pageable);

        List<AcuerdosRecord> list = page.getContent().stream()
            .map(acuerdo -> 
                new AcuerdosRecord(
                    acuerdo.numeroAcuerdo(),
                    acuerdo.fechaPublicacion(),
                    acuerdo.resumen(),
                    acuerdo.estatus())).toList();
                                   
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
