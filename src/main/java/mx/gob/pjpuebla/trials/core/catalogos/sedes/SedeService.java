package mx.gob.pjpuebla.trials.core.catalogos.sedes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.catalogos.sedes.records.SedeDomicilioRecordResponse;
import mx.gob.pjpuebla.trials.core.catalogos.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.core.catalogos.sedes.records.SedeRecord;
import mx.gob.pjpuebla.trials.core.catalogos.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Servicio para gestionar las operaciones relacionadas con las sedes.
 * Permite la creación, actualización, eliminación y consulta de sedes,
 * incluyendo la recuperación de sedes con domicilio y otros detalles
 * relacionados.
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SedeService {

    private final SedeRepository sedeRepository;
    private final DistritoRepository distritoRepository;
    private final DomicilioService domicilioService;
    private final JuzgadoRepository juzgadoRepository;
    private final OficialiaRepository oficialiaRepository;

    /**
     * Recupera todas las sedes con la posibilidad de paginación y filtrado por
     * nombre.
     * 
     * @param example  Un objeto de tipo Sede con los filtros aplicados (por
     *                 ejemplo, nombre).
     * @param pageable Objeto que contiene la información de paginación.
     * @return Un {@link Page} de {@link SedeDomicilioRecordResponse} con los
     *         resultados de la consulta.
     */
    @Transactional(readOnly = true)
    public Page<SedeDomicilioRecordResponse> getAll(Sede example, Pageable pageable) {
        return sedeRepository.findAllSedeDomicilioWithPagination(example.getNombre(), pageable);
    }

    /**
     * Busca una sede por su ID y su estado. Si no se encuentra, lanza una excepción
     * {@link NotFoundException}.
     * 
     * @param id El ID de la sede a buscar.
     * @return Un {@link SedeRecord} con los datos de la sede encontrada.
     * @throws NotFoundException Si no se encuentra la sede con el ID dado.
     */
    @Transactional(readOnly = true)
    public SedeRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return sedeRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
    }

    /**
     * Crea una nueva sede y la guarda en la base de datos. Si ya existe una sede
     * con el mismo nombre, lanza una excepción {@link ConflictException}.
     * 
     * @param sede Un objeto de tipo {@link Sede} con los datos de la nueva sede a
     *             crear.
     * @return Un {@link SedeRecordResponse} con los datos de la sede recién creada.
     * @throws ConflictException Si ya existe una sede con el mismo nombre.
     */
    public SedeRecordResponse create(Sede sede) {
        if (sedeRepository.findByNombre(sede.getNombre()).isPresent()) {
            throw new ConflictException("No pueden existir 2 sedes con el mismo nombre");
        }

        sede.setDistrito(distritoRepository.findById(sede.getDistrito().getId()).orElse(null));
        sede.setDomicilio(domicilioService.save(sede.getDomicilio()));
        sede = sedeRepository.save(sede);
        return new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado());
    }

    /**
     * Actualiza una sede existente en la base de datos.
     * Si ocurre un error de versión (optimista), lanza una excepción
     * {@link InvalidVersionException}.
     * 
     * @param sede Un objeto de tipo {@link Sede} con los datos de la sede a
     *             actualizar.
     * @return Un {@link SedeRecordResponse} con los datos de la sede actualizada.
     * @throws InvalidVersionException Si se detecta un conflicto de versiones al
     *                                 intentar actualizar la sede.
     */
    public SedeRecordResponse update(Sede sede) {
        try {
            sede.setDistrito(distritoRepository.findById(sede.getDistrito().getId()).orElse(null));
            sede.setDomicilio(domicilioService.save(sede.getDomicilio()));
            sedeRepository.save(sede);
            return new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Sede.class.getSimpleName());
        }
    }

    /**
     * Recupera todas las sedes junto con sus domicilios asociados, permitiendo la
     * paginación de los resultados.
     * 
     * @param pageable Objeto que contiene la información de paginación.
     * @return Un {@link Page} de {@link SedeDomiciliosRecord} con los resultados de
     *         la consulta.
     */
    @Transactional(readOnly = true)
    public Page<SedeDomiciliosRecord> getAllSedesAndDomicilios(Pageable pageable) {
        return sedeRepository.findSedesDomiciliosByJuzgadoId(pageable);
    }

    /**
     * Elimina una sede de la base de datos. Si la sede está asociada a un juzgado o
     * a una oficialía, lanza una excepción {@link ConstraintViolationException}.
     * 
     * @param id El ID de la sede a eliminar.
     * @throws ConstraintViolationException Si la sede está asociada a un juzgado o
     *                                      a una oficialía.
     */
    public void delete(Integer id) {
        if (juzgadoRepository.existsBySedeId(id) || oficialiaRepository.existsBySedeId(id)) {
            throw new ConstraintViolationException(
                    "No se puede eliminar la sede porque está asociada a un juzgado / oficialía",
                    "sedeId");
        } else {
            sedeRepository.deleteById(id);
        }
    }
}
