package mx.gob.pjpuebla.trials.workflow.migracion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse;
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


    @Transactional(readOnly = true)
    public Page<BandejaMigracionResponse> listar(BandejaMigracionFilter filter, Pageable pageable) {
        return migracionesRepository.findAll(MigracionesSpecs.withFilters(filter), pageable)
                .map(m -> new BandejaMigracionResponse(
                        m.getId(),
                        m.getCarpeta() != null ? m.getCarpeta().getExpediente() : null,
                        m.getEstatus().getEtiqueta(),
                        m.getObservaciones(),
                        m.getAsignacionAnterior(),
                        m.getPuestoAsignacionAnterior(),
                        m.getCarpeta() != null ? m.getCarpeta().getId() : null
                        
                ));
    }

}
