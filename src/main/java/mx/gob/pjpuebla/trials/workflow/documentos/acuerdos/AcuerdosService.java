package mx.gob.pjpuebla.trials.workflow.documentos.acuerdos;

import java.time.LocalDate;
import java.util.List;

import mx.gob.pjpuebla.trials.workflow.folios.DocumentoFoliosService;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.sentencias.records.SentenciaRecordSave;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@RequiredArgsConstructor
@Service
@Transactional
public class AcuerdosService {

    private static final String DOC_DETALL_NOT_FOUND = "Documento detalle no encontrado";
    private static final String DOC_CONT_NOT_FOUND = "Documento contenido no encontrado";
    private static final String DOC_NOT_FOUND = "Documento no encontrado";
    private final CarpetaRepository carpetaRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;
    private final MovimientoService movimientoService;
    private final PersonaService personaService;
    private final DocumentoFoliosService documentoFoliosService;

    @Transactional
    public DocumentoGenericRecord save(AcuerdoRecord acuerdo) {
        Persona persona = personaService.getAuditor();

        // Buscamos carpeta principal
        Carpeta carpeta = carpetaRepository.findById(acuerdo.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));

        // Creamos información de los rubros en documentoData
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdo.rubros());

        //Obtiene folio
        Integer folio = documentoFoliosService.getFolio(TipoDocumento.ACUERDO, persona.getJuzgado(), persona.getOficialia());

        // Creamos el nuevo documento (acuerdo=
        Documento doc = new Documento();
        doc.setCarpeta(carpeta);
        doc.setTipoDocumento(TipoDocumento.ACUERDO);
        doc.setEstatus(EstadoCarpeta.CREADO);
        doc.setData(docData);
        doc.setFolio(folio.toString());
        doc = documentoRepository.save(doc);

        // Creamos la información de documento detalle:
        DocumentoDetalle docDetalle = new DocumentoDetalle();
        docDetalle.setTipoAcuerdo(acuerdo.tipoAcuerdo());
        docDetalle.setFechaResolucion(acuerdo.fechaResolucion());
        docDetalle.setEtapaProcesal(acuerdo.etapaProcesal());
        docDetalle.setResumen(acuerdo.resumen());
        docDetalle.setDocumento(doc);
        documentoDetalleRepository.save(docDetalle);

        DocumentoContenido docContenido = new DocumentoContenido();
        docContenido.setDocumento(doc);
        docContenido.setTamanioPapel(acuerdo.tamanioPapel());
        docContenido.setTexto(acuerdo.textoEditor());
        documentoContenidoRepository.save(docContenido);

        // insertar en movimientosService
        movimientoService.createMovimento(null, doc, persona, null, EstadoCarpeta.CREADO.name());

        // Buscamos las propociones las cuales fueron marcadas para asociar el acuse:
        if (acuerdo.promocionesRelacionadas() != null) {
            for (AcuerdoPromocionesRecord promo : acuerdo.promocionesRelacionadas()) {
                Documento promocion = documentoRepository.findById(promo.id()).orElse(null);
                if (promocion != null) {
                    promocion.setAcuerdoRespuesta(doc);
                    documentoRepository.save(promocion);
                }
            }
        }

        return new DocumentoGenericRecord(doc.getId(), TipoDocumento.ACUERDO);
    }

    public List<AcuerdoPromocionesRecord> obtenerPromociones(Integer carpetaId, Integer documentoId, String tipoDocumento) {
        return documentoRepository.obtenerPromociones(carpetaId, documentoId, tipoDocumento);
    }

    public Page<AcuerdosRecord> getAcuerdos(Integer carpetaId, Pageable pageable) {
        Page<AcuerdosRecord> page = documentoRepository.findAllAcuerdosYSentenciasByCarpeta(carpetaId,
                pageable);

        List<AcuerdosRecord> list = page.getContent().stream()
                .map(acuerdo -> new AcuerdosRecord(
                        acuerdo.numeroAcuerdo(),
                        acuerdo.fechaPublicacion(),
                        acuerdo.resumen(),
                        acuerdo.estatus(),
                        acuerdo.extractoSentencia(),
                        acuerdo.tipoDocumento()))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public AcuerdoRecord publicarAcuerdo(AcuerdoRecord acuerdo) {

        Integer acuerdoId = acuerdo.acuerdoId() != null ? acuerdo.acuerdoId() : save(acuerdo).id();

        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(acuerdoId)
                .orElseThrow(() -> new NotFoundException(DOC_CONT_NOT_FOUND,
                        String.valueOf(acuerdoId)));

        DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(acuerdoId)
                .orElseThrow(() -> new NotFoundException(DOC_DETALL_NOT_FOUND,
                        String.valueOf(acuerdoId)));

        Documento documento = documentoRepository.findById(acuerdoId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, String.valueOf(acuerdoId)));

        // Actualizamos el estatus de documento a publicado esperando definirlo:
        documento.setEstatus(EstadoCarpeta.PUBLICADO);
        documentoRepository.save(documento);

        // Actualizamos fecha de publicación en documento detalle:
        documentoDetalle.setFechaPublicacion(LocalDate.now());
        documentoDetalleRepository.save(documentoDetalle);

        // Actualizamos estatus de la bandera de documento contenido:
        documentoContenido.setOficioPublicado('s');
        documentoContenidoRepository.save(documentoContenido);

        return acuerdo;
    }

    public List<AcuerdoNotificadosRecord> getTipoPartesAcuerdo(Integer carpetaId, String tipoParte) {
        return documentoRepository.findTipoPartesAcuerdo(carpetaId, tipoParte);
    }

    public Object getAcuerdoOSentencia(Integer documentoId) {

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, "documentoId"));

        DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(documentoId)
                .orElseThrow(() -> new NotFoundException(DOC_DETALL_NOT_FOUND,
                        "documentoDetalleId"));

        DocumentoContenido documentoContenido = documentoContenidoRepository.findByDocumentoId(documentoId)
                .orElseThrow(() -> new NotFoundException(DOC_CONT_NOT_FOUND,
                        "documentoContenidoId"));

        List<AcuerdoPromocionesRecord> promociones;

        if (documento.getTipoDocumento().equals(TipoDocumento.ACUERDO)) {

            promociones = obtenerPromociones(documento.getCarpeta().getId(), documentoId, "ACUERDO");
            return new AcuerdoRecord(
                    documentoId,
                    documento.getCarpeta().getId(),
                    documentoId,
                    documentoDetalle.getTipoAcuerdo(),
                    documentoDetalle.getFechaResolucion(),
                    documentoDetalle.getEtapaProcesal(),
                    documento.getData().getRubros(),
                    promociones,
                    documentoContenido.getTamanioPapel(),
                    documentoContenido.getTexto(),
                    documentoDetalle.getResumen());
        } else if (documento.getTipoDocumento().equals(TipoDocumento.SENTENCIA)) {
            promociones = obtenerPromociones(documento.getCarpeta().getId(), documentoId, "SENTENCIA");

            return new SentenciaRecordSave(
                    documentoId,
                    documentoId,
                    documento.getCarpeta().getId(),
                    documentoDetalle.getTipoSentencia(),
                    documentoDetalle.getFechaResolucion(),
                    documentoDetalle.getEtapaProcesal(),
                    documentoDetalle.getTipoResolucion(),
                    documentoDetalle.getExtractoSentencia(),
                    documentoContenido.getTamanioPapel(),
                    documentoContenido.getTexto(),
                    promociones);
        } else {
            return null;
        }

    }

    public DocumentoGenericRecord update(AcuerdoRecord acuerdo) {

        Documento documento = documentoRepository.findById(acuerdo.acuerdoId())
                .orElseThrow(() -> new NotFoundException(DOC_NOT_FOUND, "documentoId"));

        DocumentoDetalle documentoDetalle = documentoDetalleRepository.findByDocumentoId(acuerdo.acuerdoId())
                .orElseThrow(() -> new NotFoundException(DOC_DETALL_NOT_FOUND,
                        "documentoDetalleId"));

        DocumentoContenido documentoContenido = documentoContenidoRepository
                .findByDocumentoId(acuerdo.acuerdoId())
                .orElseThrow(() -> new NotFoundException(DOC_CONT_NOT_FOUND,
                        "documentoContenidoId"));

        // Actualizar datos del documento:
        documento.getData().setRubros(acuerdo.rubros());
        documentoRepository.save(documento);

        // Actualizar documento detalle:
        documentoDetalle.setTipoAcuerdo(acuerdo.tipoAcuerdo());
        documentoDetalle.setFechaResolucion(acuerdo.fechaResolucion());
        documentoDetalle.setEtapaProcesal(acuerdo.etapaProcesal());
        documentoDetalle.setResumen(acuerdo.resumen());
        documentoDetalleRepository.save(documentoDetalle);

        // Actualizar documento contenido:
        documentoContenido.setTamanioPapel(acuerdo.tamanioPapel());
        documentoContenido.setTexto(acuerdo.textoEditor());
        documentoContenidoRepository.save(documentoContenido);

        // Actualizar promociones relacionadas a null
        documentoRepository.actualizacionAcuerdoRespuesta(acuerdo.carpetaId(), acuerdo.acuerdoId());

        // volver a recorrer las promociones pero ahora las que el usuario setee
        if (acuerdo.promocionesRelacionadas() != null) {
            for (AcuerdoPromocionesRecord promo : acuerdo.promocionesRelacionadas()) {
                Documento promocion = documentoRepository.findById(promo.id()).orElse(null);
                if (promocion != null) {
                    promocion.setAcuerdoRespuesta(documento);
                    documentoRepository.save(promocion);
                }
            }
        }

        return new DocumentoGenericRecord(documento.getId(), TipoDocumento.ACUERDO);
    }

    List<AcuerdoPromocionesRecord> findPromocionesByAcuerdo(Integer acuerdoId) {
        List<Documento> promociones = documentoRepository.findByAcuerdoRespuestaId(acuerdoId);

        return promociones.stream()
                .map(p -> new AcuerdoPromocionesRecord(p.getId(),
                        (p.getTipoDocumento() == null) ? "Demanda Inicial"
                                : p.getTipoDocumento() == TipoDocumento.ACUERDO ? "Acuerdo "
                                : "Promoción " + p.getFolio(),
                        p.getRuta(), "", null))
                .toList();
    }
}
