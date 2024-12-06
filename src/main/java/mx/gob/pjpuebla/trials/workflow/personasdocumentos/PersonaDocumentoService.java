package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalle;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalleRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonaDocumentoService {

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final PersonaDetalleRepository personaDetalleRepository;
    private final DomicilioRepository domicilioRepository;

    public List<PersonaDocumentoNameRecord> getTipoPartesPrincipales(Integer carpetaId, String tipo) {
        tipo = StringUtils.capitalize(tipo.toLowerCase());
        List<PersonaDocumento> list = personaDocumentoRepository
                .findByCarpetaIdAndRolAndTipoPartesNombre(carpetaId, Rol.PRINCIPAL, tipo);

        return list.stream()
                .map(item ->
                        new PersonaDocumentoNameRecord(
                                item.getId(),
                                item.getNombre() + " " + item.getApellidoPaterno() +
                                        ((item.getApellidoMaterno() != null) ? " " + item.getApellidoMaterno() : "")
                        ))
                .toList();
    }

    public String getCorreoByPersonaDocumentoId(Integer id){
        PersonaDocumento persona = personaDocumentoRepository.findById(id).orElse(null);
        if (persona == null) {
            throw new RuntimeException("Persona no encontrada");
        }
        if (persona.getCorreoElectronico() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay correo disponible");
        }

        return persona.getCorreoElectronico();
        
    }

    public Domicilio getDomicilioByPersonaDocumentoId(Integer documentoPersonaId) {
        PersonaDetalle personaDetalle = personaDetalleRepository.findByPersonaDocumentoId(documentoPersonaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PersonaDetalle no encontrado"));
        
        if (personaDetalle.getDomicilio() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay domicilio disponible para la persona");
        }
                
        return domicilioRepository.findById(personaDetalle.getDomicilio().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domicilio no encontrado"));
    }
}
