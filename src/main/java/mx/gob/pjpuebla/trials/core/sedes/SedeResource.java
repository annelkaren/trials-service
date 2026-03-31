package mx.gob.pjpuebla.trials.core.sedes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "telefono", required = false) String telefono,
            @RequestParam(value = "estatus", required = false) Estado estatus,
            @PageableDefault(size = 20) Pageable pageable
        ) {
        return this.sedeService.getAll(key, nombre, direccion, telefono, estatus, pageable);
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
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public SedeRecordResponse create(@RequestPart(value = "sede") String sede,
                                     @RequestPart(value = "photo", required = false) MultipartFile photo)
            throws JsonProcessingException {
        Sede sedeMapper = new ObjectMapper().readValue(sede, Sede.class);
        return this.sedeService.create(sedeMapper, photo);
    }

    /**
     * Actualiza una sede existente.
     *
     * @param sede objeto {@link Sede} con la información actualizada
     * @return un {@link SedeRecordResponse} con la información de la sede actualizada
     */
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public SedeRecordResponse update(@RequestPart(value = "sede") String sede,
                                     @RequestPart(value = "photo", required = false) MultipartFile photo)
            throws JsonProcessingException {
        Sede sedeMapper = new ObjectMapper().readValue(sede, Sede.class);
        return this.sedeService.update(sedeMapper, photo);
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
