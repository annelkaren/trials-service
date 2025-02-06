package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.folios.DocumentoFoliosService;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
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

    public ReasignacionExpedienteResponseRecord reasignarExpediente(Integer carpetaParentId){
        Persona auditor = personaService.getAuditor();
        
        Oficialia oficialia = auditor.getOficialia();
        if (oficialia == null) {
            throw new NotFoundException("La persona no está relacionada con ninguna oficialía",
                    "persona.getOficialia()");
        }

        Carpeta carpetaParent = carpetaRepository.findById(carpetaParentId)
                .orElseThrow(() -> new NotFoundException("Carpeta parent no encontrada", "carpetaId: " + carpetaParentId));

        TipoJuicio tipoJuicio = tipoJuicioRepository.findById(carpetaParent.getTipoJuicio().getId())
        .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado",
                "tipoJuicioId: " + carpetaParent.getTipoJuicio().getId()));

        List<Juzgado> juzgadosRelacionados = juzgadoRepository.findJuzgadoByOficialiaId(oficialia.getId())
                .stream().filter(j -> j.getTipoJuicios().contains(tipoJuicio)).toList();

        Juzgado juzgadoCarpetaNew = juzgadoService.getJuzgado(tipoJuicio, TipoCarpeta.DEMANDA, juzgadosRelacionados);

        //Creación de carpeta.
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
        carpetaNew = carpetaRepository.save(carpetaNew);
        
        DocumentoData data = new DocumentoData()
            .setCarpetaHistorica(carpetaParentId);

        
        Documento documento = new Documento()
        .setCarpeta(carpetaNew)    
        .setData(data)
        .setPersona(auditor)
        .setFechaAsignacion(LocalDateTime.now());
       
        documento = documentoRepository.save(documento);

        //TODO: Como se obtendrean los anexos y las personas relacionadas ? se registraran nuevos registros o se consultaran por los que ya existen ?

        juzgadoService.actualizarCarga(carpetaNew.getJuzgado(), carpetaNew.getTipoCarpeta(), juzgadosRelacionados);
        movimientoService.createMovimento(carpetaNew, documento, auditor, null, EstadoCarpeta.CAPTURA.name());
        
        return new ReasignacionExpedienteResponseRecord(carpetaParentId, documento.getId(), "Carpeta reasignada exitosamente");
    }

}
