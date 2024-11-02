package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.documentos.Digitalizacion2Service;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records.DocumentoDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import java.io.IOException;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentoDetalleService {
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final DocumentoRepository documentoRepository;
    private final Digitalizacion2Service digitalizacion2Service;

    @Value("${app.root-folder}")
    private String rootFolder;

    public DigitalizacionRecord digitalizacionAcuse(DocumentoDetalleRecord documento) {
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documento.documentoId()).orElse(null);
        Documento doc = documentoRepository.findById(documento.documentoId()).orElse(null);

        if (doc != null) {
            
            DigitalizacionRecord digitalizacion = digitalizacion2Service.guardarArchivo(documento.file(), doc);

            if (docDetalle != null) {
                docDetalle.setRuta(documento.file().getOriginalFilename());
                docDetalle.setEstado(documento.estado());
                docDetalle.setComentario(documento.comentario());
                docDetalle.setFechaEntrega(documento.fechaEntrega());
                documentoDetalleRepository.save(docDetalle);
            }

            return digitalizacion;
        } else { return null; }
    }

    public byte[] getAcuse(Integer documentoId) throws IOException {

        Documento doc = documentoRepository.findById(documentoId).orElse(null);
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documentoId).orElse(null);

        if(doc != null && docDetalle != null){ return digitalizacion2Service.getDocumento(doc); }
       
        return new byte[1];
    }
}