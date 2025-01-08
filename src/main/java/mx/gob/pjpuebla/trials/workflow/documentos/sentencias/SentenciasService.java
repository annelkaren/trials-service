package mx.gob.pjpuebla.trials.workflow.documentos.sentencias;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.sentencias.records.SentenciaRecordSave;
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

        public DocumentoGenericRecord save(SentenciaRecordSave sentencia) {

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

        public DocumentoGenericRecord publicarSentencia(SentenciaRecordSave sentencia) {
                Integer acuerdoId = sentencia.sentenciaId() != null ? sentencia.sentenciaId() : save(sentencia).id();

                DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(acuerdoId)
                                .orElseThrow(() -> new NotFoundException("Documento contenido no encontrado",
                                                String.valueOf(acuerdoId)));

                DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(acuerdoId)
                                .orElseThrow(() -> new NotFoundException("contrado",
                                                String.valueOf(acuerdoId)));

                Documento documento = documentoRepository.findById(acuerdoId)
                                .orElseThrow(() -> new NotFoundException("Documento no encontrado", String.valueOf(acuerdoId)));

                // Actualizamos el estatus de documento a publicado esperando definirlo:
                documento.setEstatus(EstadoCarpeta.PUBLICADO);
                documentoRepository.save(documento);

                // Actualizamos fecha de publicación en documento detalle:
                documentoDetalle.setFechaPublicacion(LocalDate.now());
                documentoDetalleRepository.save(documentoDetalle);

                // Actualizamos estatus de la bandera de documento contenido:
                documentoContenido.setOficioPublicado('s');
                documentoContenidoRepository.save(documentoContenido);

                return new DocumentoGenericRecord(documento.getId(), documento.getTipoDocumento());
        }

        public DocumentoGenericRecord update(SentenciaRecordSave sentencia) {

                Documento documento = documentoRepository.findById(sentencia.sentenciaId())
                                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId"));

                DocumentoDetalle documentoDetalle = documentoDetalleRepository
                                .findByDocumentoId(sentencia.sentenciaId())
                                .orElseThrow(() -> new NotFoundException("contrado",
                                                "documentoDetalleId"));

                DocumentoContenido documentoContenido = documentoContenidoRepository
                                .findByDocumentoId(sentencia.sentenciaId())
                                .orElseThrow(() -> new NotFoundException("Documento contenido no encontrado",
                                                "documentoContenidoId"));

                // Actualizar documento detalle:

                documentoDetalle.setFechaResolucion(sentencia.fechaResolucion());
                documentoDetalle.setTipoSentencia(sentencia.tipoSentencia());
                documentoDetalle.setTipoResolucion(sentencia.tipoResolucion());
                documentoDetalle.setExtractoSentencia(sentencia.extractoSentencia());
                documentoDetalle.setEtapaProcesal(sentencia.etapaProcesal());
                documentoDetalleRepository.save(documentoDetalle);

                // Actualizar documento contenido:
                documentoContenido.setTamanioPapel(sentencia.tamanioPapel());
                documentoContenido.setTexto(sentencia.textoEditor());
                documentoContenidoRepository.save(documentoContenido);

                // Actualizar promociones relacionadas a null
                documentoRepository.actualizacionAcuerdoRespuesta(sentencia.carpetaId(), sentencia.sentenciaId());

                // volver a recorrer las promociones pero ahora las que el usuario setee
                
                if (sentencia.promocionesRelacionadas() != null) {
                        for (AcuerdoPromocionesRecord promo : sentencia.promocionesRelacionadas()) {
                                Documento promocion = documentoRepository.findById(promo.id()).orElse(null);
                                if (promocion != null) {
                                        promocion.setAcuerdoRespuesta(documento);
                                        documentoRepository.save(promocion);
                                }
                        }
                }
              

                return new DocumentoGenericRecord(documento.getId(), TipoDocumento.ACUERDO);
        }
}
