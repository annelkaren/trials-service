package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@Transactional
@RequiredArgsConstructor
@Service
public class AcuerdosService {

    private final CarpetaRepository carpetaRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;

    public Integer save(AcuerdoRecord acuerdo) {

        // Buscamos carpeta principal
        Carpeta carpeta = carpetaRepository.findById(acuerdo.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));

        // Creamos información de los rubros en documentoData
        DocumentoData docData = new DocumentoData()
                .setRubros(acuerdo.rubros());

        // Creamos el nuevo documento (acuerdo=
        Documento doc = new Documento()
                .setCarpeta(carpeta)
                .setTipoDocumento(TipoDocumento.ACUERDO)
                .setData(docData);
        documentoRepository.save(doc);

        // Creamos la información de documento detalle:
        DocumentoDetalle docDetalle = new DocumentoDetalle()
                .setTipoAcuerdo(acuerdo.tipoAcuerdo())
                .setFechaResolucion(acuerdo.fechaResolucion())
                .setEtapaProcesal(acuerdo.etapaProcesal())
                .setDocumento(doc);
        documentoDetalleRepository.save(docDetalle);

        DocumentoContenido docContenido = new DocumentoContenido()
                .setDocumento(doc)
                .setTamanioPapel(acuerdo.tamanioPapel())
                .setTexto(acuerdo.textoEditor());
        documentoContenidoRepository.save(docContenido);

        // Buscamos las propociones las cuales fueron marcadas para asociar el acuse:
        for (Integer promo : acuerdo.promocionesRelacionadas()) {
            Documento promocion = documentoRepository.findById(promo).orElse(null);
            if (promocion != null) {
                promocion.setAcuerdo_respuesta(doc);
                documentoRepository.save(promocion);
            }
        }

        return doc.getId();
    }

    public Integer update(AcuerdoRecord acuerdo) {

        // Creamos información de los rubros en documentoData
        DocumentoData docData = new DocumentoData()
                .setRubros(acuerdo.rubros());

        // actualizamos el documento (acuerdo)
        Documento doc = documentoRepository.findById(acuerdo.acuerdoId())
                .orElseThrow(() -> new NotFoundException("No existe un acuerdo con el id proporcionado", "documentoId"))
                .setTipoDocumento(TipoDocumento.ACUERDO)
                .setData(docData);
        documentoRepository.save(doc);

        // actualizamos la información de documento detalle:
        DocumentoDetalle docDetalle = documentoDetalleRepository.findByDocumentoId(doc.getId())
                .orElseThrow(() -> new NotFoundException("No existe un documento detalle asociado al acuerdo",
                        "documentoDetalleId"))
                .setTipoAcuerdo(acuerdo.tipoAcuerdo())
                .setFechaResolucion(acuerdo.fechaResolucion())
                .setEtapaProcesal(acuerdo.etapaProcesal());
        documentoDetalleRepository.save(docDetalle);

        DocumentoContenido docContenido = documentoContenidoRepository.findByDocumentoId(doc.getId())
                .orElseThrow(() -> new NotFoundException("No existe un documento contenido asociado al acuerdo",
                        "documentoContenidoId"))
                .setTamanioPapel(acuerdo.tamanioPapel())
                .setTexto(acuerdo.textoEditor());
        documentoContenidoRepository.save(docContenido);

        // Buscamos las propociones las cuales fueron marcadas para asociar el acuse:
        for (Integer promo : acuerdo.promocionesRelacionadas()) {
            Documento promocion = documentoRepository.findById(promo).orElse(null);
            if (promocion != null) {
                promocion.setAcuerdo_respuesta(doc);
                documentoRepository.save(promocion);
            }
        }

        return doc.getId();
    }
}
