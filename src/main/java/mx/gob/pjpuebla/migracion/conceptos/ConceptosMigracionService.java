package mx.gob.pjpuebla.migracion.conceptos;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConceptosMigracionService {
    
    private final ConceptosMigracionRepository conceptosMigracionRepository;

    public ConceptosMigracion findConceptoByClave(String clave){
        Optional<ConceptosMigracion> conceptosOptional = conceptosMigracionRepository.findByClave(clave);

        if(conceptosOptional.isPresent()){
            return conceptosOptional.get();
        }

        return null;

    }
}
