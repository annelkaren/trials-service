package mx.gob.pjpuebla.migracion.readers.domicilio;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomicilioMigracionReader {
    
    private final DomicilioMigracionRepository domicilioMigracionRepository;

    public DomicilioMigracion findByCuActorAndEstado(String cuActor){
        return domicilioMigracionRepository.findByCuActorAndEstatus(cuActor, "A").orElse(null);
    }

}
