package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
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
    private final DigitalizacionService digitalizacion2Service;
    private final DocumentoRepository documentoRepository;

    @Value("${app.root-folder}")
    private String rootFolder;

    public DigitalizacionRecord digitalizacionAcuse(DocumentoDetalleRecord documento) {
        
        Documento doc = documentoRepository.findById(documento.documentoId())
            .orElseThrow(() -> new NotFoundException("El id del documento no ha sido encontrado", "ID: " + documento.documentoId()));
            

        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documento.documentoId())
            .orElseThrow(() -> new NotFoundException("El detalle del documento no ha sido encontrado", "ID: " + doc.getId()));
        
        DigitalizacionRecord digitalizacion = digitalizacion2Service.guardarArchivo(documento.file(), documento.documentoId());



        if (docDetalle != null && doc != null) {
            docDetalle.setRuta(documento.file().getOriginalFilename());
            docDetalle.setEstado(documento.estado());
            docDetalle.setComentario(documento.comentario());
            docDetalle.setFechaEntrega(documento.fechaEntrega());
            documentoDetalleRepository.save(docDetalle);

            doc.setEstatus(EstadoCarpeta.CON_ACUSE);
            documentoRepository.save(doc);
        }

        return digitalizacion;

    }

    public byte[] getAcuse(Integer documentoId) throws IOException {
        byte[] archivo = digitalizacion2Service.getDocumento(documentoId);

        if(archivo != null){
            return archivo;
        }

        return new byte[0];
    }
}