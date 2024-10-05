package mx.gob.pjpuebla.trials.workflow.carpeta;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;

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
}
