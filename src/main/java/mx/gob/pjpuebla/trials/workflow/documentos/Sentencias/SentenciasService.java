package mx.gob.pjpuebla.trials.workflow.documentos.Sentencias;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Sentencias.records.SentenciaRecordSave;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

@Transactional
@RequiredArgsConstructor
@Service
public class SentenciasService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final CarpetaRepository carpetaRepository;

    public DocumentoGenericRecord crearSentencia(SentenciaRecordSave sentencia) {

        Carpeta carpeta = carpetaRepository.findById(sentencia.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));

        // creación del nuevo documento.
        Documento doc = new Documento()
                .setCarpeta(carpeta)
                .setTipoDocumento(TipoDocumento.SENTENCIA)
                .setEstatus(EstadoCarpeta.CREADO);

        doc = documentoRepository.save(doc);

        // Creación de la información de detalle
        DocumentoDetalle docDetalle = new DocumentoDetalle()
                .setFechaResolucion(sentencia.fechaResolucion())
                .setEtapaProcesal(sentencia.etapaProcesal())
                .setTipoSentencia(sentencia.tipoSentencia())
                .setTipoResolucion(sentencia.tipoResolucion())
                .setExtractoSentencia(sentencia.extractoSentencia())
                .setDocumento(doc);
        documentoDetalleRepository.save(docDetalle);

        // Creacón de la información de contenido:
        DocumentoContenido docContenido = new DocumentoContenido()
                .setDocumento(doc)
                .setTamanioPapel(sentencia.tamanioPapel())
                .setTexto(sentencia.textoEditor());
        documentoContenidoRepository.save(docContenido);

        // Buscamos las promociones las cuales fueron marcadas para asociar

        if (sentencia.promocionesRelacionadas() != null) {
            for (AcuerdoPromocionesRecord promo : sentencia.promocionesRelacionadas()) {
                Documento promocion = documentoRepository.findById(promo.id()).orElse(null);
                if (promocion != null) {
                    promocion.setAcuerdoRespuesta(doc);
                    documentoRepository.save(promocion);
                }
            }
        }
        return new DocumentoGenericRecord(doc.getId(), doc.getTipoDocumento());

    }

}
