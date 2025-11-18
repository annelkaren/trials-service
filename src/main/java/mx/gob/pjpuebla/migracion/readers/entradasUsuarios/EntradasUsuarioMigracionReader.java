package mx.gob.pjpuebla.migracion.readers.entradasUsuarios;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntradasUsuarioMigracionReader {

    private final EntradasUsuarioMigracionRepository entradasUsuarioMigracionRepository;

    public Optional<EntradasUsuarioMigracion> findByClaveActorAndEstatus(String claveActor) {
        return entradasUsuarioMigracionRepository
                .findByClaveActorAndEstatus(claveActor, "A") 
                .stream()
                .findFirst(); 
    }
}