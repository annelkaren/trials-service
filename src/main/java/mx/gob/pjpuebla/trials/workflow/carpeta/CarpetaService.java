package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final PersonaRepository personaRepository;
    private final DocumentoRepository documentoRepository;

    public CarpetaResponseRecord getCarpetaResponseByNumExpYearJuzgado(String expediente, Integer juzgadoId) {
        Carpeta carpeta = carpetaRepository.findByExpedienteAndJuzgadoId(expediente, juzgadoId).orElseThrow(() -> new NotFoundException("Carpeta no encontrada", expediente + " - " + juzgadoId));
        String actor = getNombrePersonaByIdAndParte(carpeta.getId(), "Actor");
        String demandado = getNombrePersonaByIdAndParte(carpeta.getId(), "Demandado");
        return new CarpetaResponseRecord(carpeta.getId(), actor, demandado);
    }

    protected String getNombrePersonaByIdAndParte(Integer id, String parte) {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        PersonaDocumentoRecord persona = personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, rol);

        String nombre = persona.nombre() != null ? persona.nombre() : "";
        String apellidoPaterno = persona.apellidoPaterno() != null ? persona.apellidoPaterno() : "";
        String apellidoMaterno = persona.apellidoMaterno() != null ? persona.apellidoMaterno() : "";

        return String.format("%s %s %s", nombre, apellidoPaterno, apellidoMaterno).trim();
    }

    @Transactional(readOnly = true)
    public List<ApelacionRecordResponse> getPersonasDocumentoByCarpetaId(Integer carpetaId) {
        return personaDocumentoRepository.findPersonaDocumentoByCarpetaId(carpetaId);
    }

    public BandejaRecepcionRecord getBandejaRecepcionByDocumentoId(Long personaId, Integer documentoId) {
        // Buscar y validar la existencia de la persona y el documento
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new NotFoundException("No se encontró la persona asociada al personaId: " + personaId, "personaId"));
        
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("No se encontró el documento asociado al documentoId: " + documentoId, "documentoId"));
    
        // Verificar permisos de acceso al juzgado
        Juzgado juzgado = persona.getJuzgado();
        if (!juzgado.getId().equals(documento.getCarpeta().getJuzgado().getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permiso para visualizar esta información");
        }
    
        // Buscar la bandeja de recepción por folio
        BandejaRecepcionRecord bandeja = carpetaRepository.findByFolioAndJuzgado_Name(documento.getId());
        if (bandeja == null) {
            throw new NotFoundException("No se encontró la carpeta con el documentoId: " + documentoId, "documentoId");
        }
    
        // Obtener y devolver los anexos de la bandeja de recepción
        List<AnexoBandejaRecepcionRecord> anexos = carpetaRepository.findAnexosByDocumentoId(documento.getId());
        return new BandejaRecepcionRecord(
                documento.getId(),
                bandeja.folio(),
                bandeja.expediente(),
                bandeja.tipo(),
                bandeja.rutaDigitalizacion(),
                anexos
        );
    }
    

}
