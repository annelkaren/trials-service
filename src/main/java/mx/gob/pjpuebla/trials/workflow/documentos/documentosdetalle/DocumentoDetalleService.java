package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;

@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoDetalleService {
    private final DocumentoDetalleRepository documentoDetalleRepository;

    public Integer digitalizacionAcuse(DocumentoDetalleRecord documento){
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documento.documentoId()).orElse(null);
       
        if(docDetalle != null) {
            docDetalle.setRuta(documento.file().getOriginalFilename());
            docDetalle.setEstado(documento.estado());
            docDetalle.setComentario(documento.comentario());
            docDetalle.setFechaEntrega(documento.fechaEntrega());
            documentoDetalleRepository.save(docDetalle);
        }


        return 1;
    }
}
