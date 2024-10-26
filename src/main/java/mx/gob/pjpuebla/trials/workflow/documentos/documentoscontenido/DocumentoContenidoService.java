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
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;

@Transactional
@RequiredArgsConstructor
@Service
public class DocumentoContenidoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;
    private final InstitucionRepository institucionRepository;

    public DocumentoOficioDigitalizacionRecord getDataDocumentoDigitalizacion(Integer documentoId) {
        // Obtenemmos registro del documento
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId: " + documentoId));
        String expediente = doc.getCarpeta() != null ? doc.getCarpeta().getExpediente() : "";

        // TODO: actualizar fecha de emisión y asunto con los datos correctos, se coloca
        // vacio ya que la tabla aun no existe

        LocalDate fechaEmision = null;
        LocalDate fechaEntrega = null;
        String asunto = "";

        // TODO: asignar vaiores cuando se tengan disponibles
        String nombreAcuse = "";
        String comentario = "";

        // Obtenemos texto del editor:
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumento_Id(documentoId)
                .orElse(null);
        String tamanioPapel = "";
        String textoEditor = "";
        String existeOficio = ""; 

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

        // TODO: Actualizar asunto cuando se tenga la tabla en donde se guardara.

        // Actualizamos o creamos la parte de documento contenido
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumento_Id(oficio.idOficio())
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

    public Integer publicarCancelarOficio(Integer documentoId, String oficioPublicado){

        //Actualizamos o creamos la parte de documento contenido
        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumento_Id(documentoId).orElse(null);

        if(documentoContenido == null){
            documentoContenido = new DocumentoContenido();
        }

        documentoContenido.setOficioPublicado(oficioPublicado);
        documentoContenidoRepository.save(documentoContenido);

        return 1;
   }
}
