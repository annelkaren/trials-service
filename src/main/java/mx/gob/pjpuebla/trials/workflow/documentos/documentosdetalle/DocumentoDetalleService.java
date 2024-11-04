package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final DocumentoRepository documentoRepository;
    private final DigitalizacionService digitalizacion2Service;

    @Value("${app.root-folder}")
    private String rootFolder;

    public DigitalizacionRecord digitalizacionAcuse(DocumentoDetalleRecord documento) {
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(documento.documentoId()).orElse(null);
        DigitalizacionRecord digitalizacion = digitalizacion2Service.guardarArchivo(documento.file(), documento.documentoId());

        if (docDetalle != null) {
            docDetalle.setRuta(documento.file().getOriginalFilename());
            docDetalle.setEstado(documento.estado());
            docDetalle.setComentario(documento.comentario());
            docDetalle.setFechaEntrega(documento.fechaEntrega());
            documentoDetalleRepository.save(docDetalle);
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