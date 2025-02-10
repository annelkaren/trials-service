package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReactivacionExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReasignacionExpedienteResponseRecord;

@Transactional
@RequiredArgsConstructor
@Service
public class ReasignacionExpedienteService {
    private final CarpetaRepository carpetaRepository;
    private final PersonaService personaService;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final JuzgadoService juzgadoService;
    private final DocumentoService documentoService;
    private final DocumentoRepository documentoRepository;
    private final MovimientoService movimientoService;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final AnexoRepository anexoRepository;
        

    @Transactional
    public ReasignacionExpedienteResponseRecord reasignarExpediente(Integer carpetaParentId) {
        Persona auditor = personaService.getAuditor();

        Oficialia oficialia = auditor.getOficialia();
        if (oficialia == null) {
            throw new NotFoundException("La persona no está relacionada con ninguna oficialía",
                    "persona.getOficialia()");
        }

        Carpeta carpetaParent = carpetaRepository.findById(carpetaParentId)
                .orElseThrow(
                        () -> new NotFoundException("Carpeta parent no encontrada", "carpetaId: " + carpetaParentId));

        Documento documentoParent = documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaParentId);
        Boolean expedienteReasignado = documentoParent.getData().getExpedienteReasignado() != null ? documentoParent.getData().getExpedienteReasignado() : false;

        if(expedienteReasignado){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El expediente ya ha sido reasignado.");
        }


        TipoJuicio tipoJuicio = tipoJuicioRepository.findById(carpetaParent.getTipoJuicio().getId())
                .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado",
                        "tipoJuicioId: " + carpetaParent.getTipoJuicio().getId()));

        List<Juzgado> juzgadosRelacionados = juzgadoRepository.findJuzgadoByOficialiaId(oficialia.getId())
                .stream().filter(j -> j.getTipoJuicios().contains(tipoJuicio)).toList();

        Juzgado juzgadoCarpetaNew = juzgadoService.getJuzgado(tipoJuicio, TipoCarpeta.DEMANDA, juzgadosRelacionados);

        // Creación de carpeta.
        Carpeta carpetaNew = new Carpeta()
                .setTipoJuicio(tipoJuicio)
                .setTipoCarpeta(TipoCarpeta.DEMANDA)
                .setJuzgado(juzgadoCarpetaNew)
                .setFolio(documentoService.getFolio("D"))
                .setExpediente(documentoService.generateNumExpediente(juzgadoCarpetaNew, TipoCarpeta.DEMANDA))
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setPersona(auditor)
                .setFechaAsignacion(LocalDateTime.now());
        Carpeta carpetaSaved = carpetaRepository.save(carpetaNew);

        // creación del documento
        DocumentoData data = new DocumentoData()
                .setCarpetaHistorica(carpetaParentId);

        Documento documento = new Documento()
                .setCarpeta(carpetaNew)
                .setData(data)
                .setPersona(auditor)
                .setFechaAsignacion(LocalDateTime.now());
        Documento documentoSaved = documentoRepository.save(documento);

        //creación de personasDocumentos:
        List<PersonaDocumento> personaDocumentos = personaDocumentoRepository.findByCarpetaId(carpetaParentId)
                .stream()
                .map(personaDocumento -> new PersonaDocumento()
                        .setNombre(personaDocumento.getNombre())
                        .setApellidoPaterno(personaDocumento.getApellidoPaterno())
                        .setApellidoMaterno(personaDocumento.getApellidoMaterno())
                        .setPseudonimo(personaDocumento.getPseudonimo())
                        .setTipoPersona(personaDocumento.getTipoPersona())
                        .setRol(personaDocumento.getRol())
                        .setIne(personaDocumento.getIne())
                        .setCurp(personaDocumento.getCurp())
                        .setCorreoElectronico(personaDocumento.getCorreoElectronico())
                        .setCelular(personaDocumento.getCelular())
                        .setCarpeta(carpetaSaved)
                        .setDomicilio(personaDocumento.getDomicilio())
                        .setTipoPartes(personaDocumento.getTipoPartes())
                        .setTipoNotificacion(personaDocumento.getTipoNotificacion())
                        .setCorreoNotificacion(personaDocumento.getCorreoNotificacion())
                        .setDomicilio(personaDocumento.getDomicilio())
                )
                .toList();
        
        // Guardar todos los registros modificados de una sola vez
        personaDocumentoRepository.saveAll(personaDocumentos);

        //creación de anexos:
        List<Anexo> anexosCarpeta = anexoRepository.findAnexosByCarpetaIdOrDocumentoId(null, carpetaParentId)
                .stream()
                .map(anexo -> new Anexo()
                        .setDocumento(documentoSaved)
                        .setEstado(anexo.estado())
                        .setNombre(anexo.nombre()))
                .toList();
        
        anexoRepository.saveAll(anexosCarpeta);

        

        //Actualizanos jData del documento de la carpeta parent :
        documentoParent.getData().setExpedienteReasignado(true);
        documentoRepository.save(documentoParent);

        juzgadoService.actualizarCarga(carpetaNew.getJuzgado(), carpetaNew.getTipoCarpeta(), juzgadosRelacionados);
        movimientoService.createMovimento(carpetaNew, documento, auditor, null, EstadoCarpeta.CAPTURA.name());

        return new ReasignacionExpedienteResponseRecord(carpetaParentId, documento.getId(),
                "Carpeta reasignada exitosamente");
    }

    public ReactivacionExpedienteRecord reactivacionExpediente(Integer carpetaId){
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId: " + carpetaId));

        Documento documento = documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaId);

        Juzgado juzgado = carpeta.getJuzgado();

        if(documento.getData().getExpedienteReasignado()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "No es posible reactivar el expediente ya que ha sido reasignado.");
        }

        if(!carpeta.getEstatus().equals(EstadoCarpeta.ARCHIVO_JUDICIAL)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                 "No es posible reactivar el expediente si no se encuentra en archivo judicial.");
        }

        if(!juzgado.getEstado().equals(Estado.ACTIVE)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                 "No es posible reactivar el expediente ya que el juzgado no se encuentra activo.");
        }

        carpeta.setEstatus(EstadoCarpeta.RECEPCION);

        return new ReactivacionExpedienteRecord(carpetaId, "Se ha reactivado el expediente nuevamente.");
    }

}
