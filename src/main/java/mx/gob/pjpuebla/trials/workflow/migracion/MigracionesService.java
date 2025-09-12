package mx.gob.pjpuebla.trials.workflow.migracion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.InternalServerError;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigracionesService {

    private final MigracionesRepository migracionesRepository;
    private final PersonaService personaService;
    private final MovimientoService movimientoService;
    private final CarpetaService carpetaService;

    public Migraciones createMigraciones(EstadoMigracion estadoMigracion, String observaciones,
            String asignacionAnterior, String puestoAsignacionAnterior, Juzgado juzgado, Carpeta carpeta) {

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
                        personaService.getNamePersona(m.getAudit().getUsuarioAlta()),
                        m.getObservaciones(),
                        m.getAsignacionAnterior(),
                        m.getPuestoAsignacionAnterior(),
                        m.getCarpeta() != null ? m.getCarpeta().getId() : null,
                        m.getCarpeta().getConcepto().getNombre(),
                        m.getCarpeta().getConcepto().getDias()
                ));
    }

    @Transactional
    public ApiResponse<String> turnarExpedienteMigrado(Integer migracionId, Long personaId) {
        // 1) Cargar migración
        Migraciones migracion = migracionesRepository.findById(migracionId)
            .orElseThrow(() -> new NotFoundException("No fue posible encontrar el registro de migración", migracionId.toString()));
      
        // 2) Validar estado de la migración
        if (migracion.getEstatus() != EstadoMigracion.MIGRADO_COMPLETADO) {
            throw new NotFoundException("La migración no está en un estado turnable.", migracionId.toString());
        }

        // 3) Cargar carpeta y dependencias
        Carpeta carpeta = migracion.getCarpeta();
        Concepto concepto = carpeta.getConcepto();
        Persona personaAsignada = personaService.findPersonaById(personaId);
        if (personaAsignada == null) {
             throw new NotFoundException("No existe la persona indicada.", personaId.toString());
        }

        // 4) Idempotencia: si ya está asignada a esa persona, responde OK
        if (carpeta.getEstatus() == EstadoCarpeta.ASIGNADO) {
             return new ApiResponse<>(true, "El expediente ya estaba turnado.", "SUCCESS_ALREADY_ASSIGNED", 200, "", LocalDateTime.now());
        }

        // 5) Actualizar carpeta
        carpeta.setEstatus(EstadoCarpeta.ASIGNADO);
        carpeta.setPersona(personaAsignada);
        carpetaService.save(carpeta); 

        // 6) verificar si hay piezas relacionadas con esta carpeta:
        List<Carpeta> piezas = carpetaService.findPiezasByCarpeta(carpeta);

        piezas.forEach(pieza -> { pieza.setPersona(personaAsignada);  });
        carpetaService.saveAll(piezas);

        // 6) Crear movimiento
        Integer dias = concepto.getDias(); 
        String duration = (dias != null ? dias + "d" : null);

        Movimiento movimiento = movimientoService.createMovimentoTurnado(
                carpeta,
                null, 
                personaAsignada,
                null, 
                EstadoCarpeta.ASIGNADO.name(),
                concepto.getNombre(),
                null, 
                duration);

        if (movimiento == null) {
            throw new InternalServerError("No se pudo registrar el movimiento de turnado.");
        }

        // 7) Actualizar estatus de la migración
        migracion.setEstatus(EstadoMigracion.EXPEDIENTE_TURNADO);
        migracion.setPersonaTurnado(personaAsignada);
        migracion.setObservaciones("Se ha turnado el expediente a " +  personaService.getNamePersona(personaAsignada.getUsuario()) );
        migracionesRepository.save(migracion);

        return new ApiResponse<>(true, "El expediente ha sido turnado con éxito.", "SUCCESS", 201, "", LocalDateTime.now());
    }

}
