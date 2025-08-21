package mx.gob.pjpuebla.trials.workflow.migracion;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigracionesService {
    
    private final MigracionesRepository migracionesRepository;


    public Migraciones createMigraciones(EstadoMigracion estadoMigracion, String observaciones, String asignacionAnterior, String puestoAsignacionAnterior, Juzgado juzgado, Carpeta carpeta){

         Migraciones migracion = new Migraciones()
            .setVersion(0)
            .setEstatus(estadoMigracion)
            .setObservaciones(observaciones)
            .setAsignacionAnterior(asignacionAnterior)
            .setPuestoAsignacionAnterior(puestoAsignacionAnterior)
            .setJuzgado(juzgado)
            .setCarpeta(carpeta);

        return migracionesRepository.save(migracion);
    }

}
