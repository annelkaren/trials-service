package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@RequiredArgsConstructor
@Service
@Transactional
public class AcuerdosService {

    private final CarpetaRepository carpetaRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;

    @Transactional
    public Documento save(AcuerdoRecord acuerdo) {

        // Buscamos carpeta principal
        Carpeta carpeta = carpetaRepository.findById(acuerdo.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));
        // Carpeta carpeta = carpetaRepository.getReferenceById(acuerdo.carpetaId());

        // Creamos información de los rubros en documentoData
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdo.rubros());

        // Creamos el nuevo documento (acuerdo=
        Documento doc = new Documento();
                doc.setCarpeta(carpeta);
                doc.setTipoDocumento(TipoDocumento.ACUERDO);
                doc.setData(docData);
        doc = documentoRepository.save(doc);
        /* 
        // Creamos la información de documento detalle:
        DocumentoDetalle docDetalle = new DocumentoDetalle();
            docDetalle.setTipoAcuerdo(acuerdo.tipoAcuerdo());
            docDetalle.setFechaResolucion(acuerdo.fechaResolucion());
            docDetalle.setEtapaProcesal(acuerdo.etapaProcesal());
            docDetalle.setDocumento(doc);
        documentoDetalleRepository.save(docDetalle);


        DocumentoContenido docContenido = new DocumentoContenido();
                docContenido.setDocumento(doc);
                docContenido.setTamanioPapel(acuerdo.tamanioPapel());
                docContenido.setTexto(acuerdo.textoEditor());
        documentoContenidoRepository.save(docContenido);
        */
        // Buscamos las propociones las cuales fueron marcadas para asociar el acuse:
        if (acuerdo.promocionesRelacionadas() != null) {
            for (Integer promo : acuerdo.promocionesRelacionadas()) {
                Documento promocion = documentoRepository.findById(promo).orElse(null);
                if (promocion != null) {
                    promocion.setAcuerdo_respuesta(doc);
                    documentoRepository.save(promocion);
                }
            }
        }

        return doc;
    }

    public List<AcuerdoPromocionesRecord> obtenerPromociones(Integer carpetaId) {
        return documentoRepository.obtenerPromociones(carpetaId);
    }
}
