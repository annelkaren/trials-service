package mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;

@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoContenidoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final InstitucionRepository institucionRepository;

    public DocumentoOficioDigitalizacionRecord getDataDocumentoDigitalizacion(Integer documentoId) {
        // Obtenemmos registro del documento
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId: " + documentoId));
        String expediente = doc.getCarpeta() != null ? doc.getCarpeta().getExpediente() : "";

        // TODO: asignar vaiores cuando se tengan disponibles
        String nombreAcuse = "";
        String comentario = "";

        // Obtenemos texto del editor:
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(documentoId).orElse(null);
        char tamanioPapel = ' ';
        String textoEditor = "";
        char existeOficio = ' '; 

        //Obtenemos información detallada del documento
        DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(documentoId).orElse(null);;
        LocalDate fechaEmision = null;
        LocalDate fechaEntrega = null;
        String asunto = "";

        if(documentoDetalle != null){
            fechaEmision = documentoDetalle.getFechaEmision();
            fechaEntrega = documentoDetalle.getFechaEntrega();
            asunto = documentoDetalle.getAsunto();  
        }

        if (documentoContenido != null) {
            tamanioPapel = documentoContenido.getTamanioPapel();
            textoEditor = documentoContenido.getTexto();
            existeOficio = documentoContenido.getOficioPublicado();
        }

        return new DocumentoOficioDigitalizacionRecord(
                doc.getFolio(),
                expediente,
                fechaEmision,
                doc.getId(),
                doc.getInstitucion().getId(),
                fechaEntrega,
                doc.getEstatus(),
                asunto,
                tamanioPapel,
                existeOficio,
                nombreAcuse,
                comentario,
                textoEditor);

    }

    public Integer cancelarOficio(Integer documentoId) {
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId: " + documentoId));

        doc.setEstatus(EstadoCarpeta.CANCELADO);
        documentoRepository.save(doc);

        return 1;
    }

    public DocumentoOficioDigitalizacionRecord updateDocumentoOficioDigitalizacion(
            DocumentoOficioDigitalizacionRecord oficio) {
        Documento doc = documentoRepository.findById(oficio.idOficio())
                .orElseThrow(
                        () -> new NotFoundException("Documento no encontrado", "documentoId: " + oficio.idOficio()));

        Institucion institucion = institucionRepository.findById(oficio.dependencia())
                .orElseThrow(
                        () -> new NotFoundException("Documento no encontrado", "documentoId: " + oficio.dependencia()));

        doc.setInstitucion(institucion);
        documentoRepository.save(doc);

        // Actualizamos asunto:
         //Obtenemos información detallada del documento
        DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(oficio.idOficio()).orElse(null);;
        documentoDetalle.setAsunto(oficio.asunto());
        documentoDetalleRepository.save(documentoDetalle);

        // Actualizamos o creamos la parte de documento contenido
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(oficio.idOficio())
                .orElse(null);

        if (documentoContenido == null) {
            documentoContenido = new DocumentoContenido();
        }

        documentoContenido.setDocumento(doc);
        documentoContenido.setTamanioPapel(oficio.tamanioPapel());
        documentoContenido.setTexto(oficio.textoEditor());

        documentoContenidoRepository.save(documentoContenido);

        return oficio;
    }

    public Integer publicarCancelarOficio(Integer documentoId, char oficioPublicado){

        //Actualizamos o creamos la parte de documento contenido
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(documentoId).orElse(null);

        if(documentoContenido == null){
            documentoContenido = new DocumentoContenido();
        }

        documentoContenido.setOficioPublicado(oficioPublicado);
        documentoContenidoRepository.save(documentoContenido);

        return 1;
   }
}
