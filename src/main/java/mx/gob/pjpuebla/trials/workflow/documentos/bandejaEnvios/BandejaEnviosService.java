package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnvioRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosCambioEstatus;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvio;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Transactional
@RequiredArgsConstructor
@Service
public class BandejaEnviosService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final PersonaRepository personaRepository;
    private final PersonaService personaService;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public Page<BandejaEnviosRecord> getAllBandejaEnviados(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona persona = personaService.getAuditor();
        List<Juzgado> juzgados = List.of();

        Boolean isOficialMayorJuzgadoOrAuxiliar =  roleService.hasRole(persona.getUsuario(), "OFICIAL_MAYOR_JUZGADO") || roleService.hasRole(persona.getUsuario(), "AUXILIAR_OFICIAL_MAYOR_JUZGADO"); 
        Boolean isOficialMayorOficialia = roleService.hasRole(persona.getUsuario(), "OFICIAL_MAYOR_OFICIALIA");

        if(isOficialMayorJuzgadoOrAuxiliar){
            juzgados = List.of(persona.getJuzgado());
        }
        if(isOficialMayorOficialia){
            juzgados = persona.getOficialia().getJuzgados();
        }


        List<BandejaEnviosRecord> response = documentoRepository.findAllOficiosBandejaSalida(key, isOficialMayorOficialia, juzgados);

        return new PageImpl<>(response, pageable, response.size());
    }

    public BandejaEnvioRecordResponse actualizarEstatusOficio(BandejaEnviosCambioEstatus bandejaEnvios) {
        Persona persona = null;

        System.out.println(bandejaEnvios.estadoEnvio().name());
        if (!bandejaEnvios.estadoEnvio().equals(EstadoEnvio.ENVIADO)) {
            persona = personaRepository.findById((long) bandejaEnvios.mensajero()).orElse(null);
        }

        List<DocumentoDetalle> documentosDetalles = documentoDetalleRepository.findAllByDocumentoIdIn(bandejaEnvios.oficiosIds());

        // Actualizamos todos los documentos
        for (DocumentoDetalle documentoDetalle : documentosDetalles) {
            documentoDetalle.setEstadoEnvio(bandejaEnvios.estadoEnvio());
            documentoDetalle.setPersona(persona);
        }

        // Guardamos todos los cambios de una vez (batch)
        documentoDetalleRepository.saveAll(documentosDetalles);

        return new BandejaEnvioRecordResponse(200, "Estatus actualizado correctamente");

    }



}
