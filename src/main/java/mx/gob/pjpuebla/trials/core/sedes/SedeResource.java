package mx.gob.pjpuebla.trials.core.sedes;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar las sedes.
 * Proporciona operaciones para obtener, crear, actualizar y eliminar sedes.
 * 
 * <p>Este controlador requiere autenticación a través de Keycloak.</p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/sedes")
@SecurityRequirement(name = "Keycloak")
public class SedeResource {

    private final SedeService sedeService;

    /**
     * Obtiene una lista paginada de sedes, con la opción de filtrar por nombre.
     * 
     * @param pageable configuración de paginación (tamaño por defecto de 20)
     * @param nombre (opcional) nombre de la sede a filtrar
     * @return una página de {@link SedeDomicilioRecordResponse}
     */
    @GetMapping
    public Page<SedeDomicilioRecordResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.sedeService.getAll(new Sede().setNombre(nombre), pageable);
    }

    /**
     * Obtiene una sede por su ID.
     * 
     * @param id identificador de la sede
     * @return un objeto {@link SedeRecord} con la información de la sede
     */
    @GetMapping("/{id}")
    public SedeRecord getById(@PathVariable Integer id) {
        return this.sedeService.findById(id);
    }

    /**
     * Crea una nueva sede.
     * 
     * @param sede objeto {@link Sede} que contiene la información de la nueva sede
     * @return un {@link SedeRecordResponse} con la información de la sede creada
     */
    @PostMapping
    public SedeRecordResponse create(@RequestBody @Valid Sede sede) {
        return this.sedeService.create(sede);
    }

    /**
     * Actualiza una sede existente.
     * 
     * @param sede objeto {@link Sede} con la información actualizada
     * @return un {@link SedeRecordResponse} con la información de la sede actualizada
     */
    @PutMapping
    public SedeRecordResponse update(@RequestBody @Valid Sede sede) {
        return this.sedeService.update(sede);
    }

    /**
     * Elimina una sede por su ID.
     * 
     * @param id identificador de la sede a eliminar
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.sedeService.delete(id);
    }

    /**
     * Obtiene una lista paginada de todas las sedes junto con sus domicilios.
     * 
     * @param pageable configuración de paginación (tamaño por defecto de 20)
     * @return una página de {@link SedeDomiciliosRecord}
     */
    @GetMapping("/domicilios")
    public Page<SedeDomiciliosRecord> getAllDomicilosOfSede(@PageableDefault(size = 20) Pageable pageable) {
        return this.sedeService.getAllSedesAndDomicilios(pageable);
    }
}
