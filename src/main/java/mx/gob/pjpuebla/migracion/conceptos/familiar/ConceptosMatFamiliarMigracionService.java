package mx.gob.pjpuebla.migracion.conceptos.familiar;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConceptosMatFamiliarMigracionService {
    
    private final ConceptosMatFamiliarMigracionRepository conceptosMatFamiliarMigracionRepository;

    public ConceptosMatFamiliarMigracion findConceptoMatFamiliarByClave(String clave){
        Optional<ConceptosMatFamiliarMigracion> conceptosOptional = conceptosMatFamiliarMigracionRepository.findByClave(clave);

        if(conceptosOptional.isPresent()){
            return conceptosOptional.get();
        }

        return null;

    }
}
