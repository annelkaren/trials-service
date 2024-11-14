package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import java.util.List;

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
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdosRecord;
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

    private final CarpetaRepository carpetaRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final DocumentoContenidoRepository documentoContenidoRepository;
    private final MovimientoService movimientoService;
    private final PersonaService personaService;
    
    //TODO: Verificar si el flujo es el correcto.
    @Transactional
    public DocumentoGenericRecord save(AcuerdoRecord acuerdo) {

        // Buscamos carpeta principal
        Carpeta carpeta = carpetaRepository.findById(acuerdo.carpetaId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId"));
        
        // Creamos información de los rubros en documentoData
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdo.rubros());

        // Creamos el nuevo documento (acuerdo=
        Documento doc = new Documento();
                doc.setCarpeta(carpeta);
                doc.setTipoDocumento(TipoDocumento.ACUERDO);
                doc.setEstatus(EstadoCarpeta.CREADO);
                doc.setData(docData);
        doc = documentoRepository.save(doc);
       
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


        //insertar en movimientosService 
        Persona persona = personaService.getAuditor();
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

    public List<AcuerdoPromocionesRecord> obtenerPromociones(Integer carpetaId) {
        return documentoRepository.obtenerPromociones(carpetaId);
    }

    public Page<AcuerdosRecord> getAcuerdos(Integer carpetaId, Pageable pageable){
        Page<AcuerdosRecord> page = documentoRepository.findAllAcuerdosByCarpeta(carpetaId, pageable);

        List<AcuerdosRecord> list = page.getContent().stream()
            .map(acuerdo -> 
                new AcuerdosRecord(
                    acuerdo.numeroAcuerdo(),
                    acuerdo.fechaResolucion(),
                    acuerdo.resumen(),
                    acuerdo.estatus())).toList();
                                   
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}

