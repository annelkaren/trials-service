package mx.gob.pjpuebla.trials.core.instituciones;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecordResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Controlador REST para gestionar las instituciones.
 * Expone servicios para crear, actualizar, eliminar y consultar instituciones.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/instituciones")
@SecurityRequirement(name = "keycloak")
public class InstitucionResource {

    private final InstitucionService institucionService;

    /**
     * Obtiene una lista paginada de instituciones, con la opción de filtrar por nombre.
     *
     * @param pageable Información de paginación para la consulta.
     * @param nombre   Filtro opcional para buscar instituciones por nombre. Si no se especifica, se devuelven todas las instituciones.
     * @return Página de resultados con instituciones que coinciden con los filtros proporcionados.
     */
    @GetMapping
    public Page<InstitucionRecord> getAll(
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(value = "nombre", required = false) String nombre) {
        return this.institucionService.getAll(new Institucion().setNombre(nombre), pageable);
    }

    /**
     * Obtiene los detalles de una institución por su identificador.
     *
     * @param id El identificador único de la institución.
     * @return La respuesta con los detalles de la institución solicitada.
     */
    @GetMapping("/{id}")
    public InstitucionRecordResponse getById(@PathVariable Integer id) {
        return this.institucionService.findById(id);
    }

    /**
     * Crea una nueva institución en el sistema.
     *
     * @param institucion El objeto Institucion con la información de la nueva institución a crear.
     * @return El identificador de la nueva institución creada.
     */
    @PostMapping
    public Integer create(@RequestBody @Valid Institucion institucion) {
        return this.institucionService.create(institucion);
    }

    /**
     * Actualiza los detalles de una institución existente.
     *
     * @param institucion El objeto Institucion con la nueva información para actualizar la institución.
     * @return El identificador de la institución actualizada.
     */
    @PutMapping
    public Integer update(@RequestBody Institucion institucion) {
        return this.institucionService.update(institucion);
    }

    /**
     * Elimina una institución del sistema por su identificador.
     *
     * @param id El identificador de la institución a eliminar.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.institucionService.delete(id);
    }

    /**
     * Obtiene una lista paginada de instituciones activas con la opción de filtrar por nombre.
     * El filtro de nombre es opcional y permite autocompletar la búsqueda de instituciones.
     *
     * @param pageable Información de paginación para la consulta.
     * @param nombre   Filtro opcional para buscar instituciones por nombre. Si no se especifica, se devuelven todas las instituciones activas.
     * @return Página de resultados con instituciones activas que coinciden con los filtros proporcionados.
     */
    @GetMapping("/autocomplete")
    public Page<InstitucionRecord> getAllByEstadoAutocomplete(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {

        return this.institucionService.getAllByEstadoAutocomplete(new Institucion().setNombre(nombre), pageable);
    }

    /**
     * Obtiene todas las instituciones de tipo "Tribunal Federal".
     *
     * @return Lista de instituciones de tipo "Tribunal Federal".
     */
    @GetMapping("/tribunales")
    public List<InstitucionRecord> getByTipoInstitucion() {
        return this.institucionService.findByTipoInstitucion("Tribunal Federal");
    }
}
