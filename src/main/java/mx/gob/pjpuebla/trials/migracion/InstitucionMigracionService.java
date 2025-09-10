package mx.gob.pjpuebla.trials.migracion;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionRepository;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionService;

@Service
@RequiredArgsConstructor
public class InstitucionMigracionService {
    private final InstitucionRepository institucionRepository;
    private final InstitucionService institucionService;


    

}
