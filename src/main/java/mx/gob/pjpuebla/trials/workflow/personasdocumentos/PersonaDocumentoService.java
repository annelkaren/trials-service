package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonaDocumentoService {

    private final PersonaDocumentoRepository personaDocumentoRepository;

    public List<PersonaDocumentoNameRecord> getTipoPartesPrincipales(Integer carpetaId, String tipo) {
        tipo = StringUtils.capitalize(tipo.toLowerCase());
        List<PersonaDocumento> list = personaDocumentoRepository
                .findByCarpetaIdAndRolAndTipoPartesNombre(carpetaId, Rol.PRINCIPAL, tipo);

        List<PersonaDocumentoNameRecord> partes = list.stream()
                .map(item ->
                        new PersonaDocumentoNameRecord(
                                item.getId(),
                                item.getNombre() + " " + item.getApellidoPaterno() +
                                        ((item.getApellidoMaterno() != null) ? " " + item.getApellidoMaterno() : "")
                        ))
                .toList();
        return partes;
    }

}
