package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repositorio para la entidad {@link Sede}.
 * Proporciona métodos para interactuar con la base de datos relacionados con las sedes y sus domicilios asociados.
 */
@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {

    /**
     * Recupera una sede por su ID y su estado, devolviendo un {@link SedeRecord} con los datos completos.
     * Si no se encuentra la sede, devuelve un {@link Optional#empty()}.
     *
     * @param id El ID de la sede a buscar.
     * @param estados Una lista de estados para filtrar las sedes activas o inactivas.
     * @return Un {@link Optional} con el {@link SedeRecord} de la sede encontrada, o {@link Optional#empty()} si no se encuentra.
     */
    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord(s.id, s.version, s.nombre, s.estado, s.tipo, s.telefono, s.extension,
            new mx.gob.pjpuebla.trials.core.distritos.DistritoRecord(dis.id, dis.nombre),
            new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior,
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia))
            FROM Sede s
            LEFT JOIN s.domicilio dom
            LEFT JOIN s.distrito dis
            WHERE s.id =:id AND s.estado IN :estados
            """)
    Optional<SedeRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    /**
     * Recupera todas las sedes junto con sus domicilios asociados, permitiendo la paginación de los resultados.
     *
     * @param pageable Objeto que contiene la información de paginación.
     * @return Un {@link Page} de {@link SedeDomiciliosRecord} con los resultados de la consulta.
     */
    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord(
                s.id,  s.nombre, d.calle,d.interior, d.exterior, d.colonia, d.codigoPostal, d.municipio,d.estadoRepublica,
                d.referencia, d.localidad
            )
            FROM Sede s
            JOIN s.domicilio d
            """)
    Page<SedeDomiciliosRecord> findSedesDomiciliosByJuzgadoId(Pageable pageable);

    /**
     * Busca una sede por su nombre.
     * 
     * @param nombre El nombre de la sede.
     * @return Un {@link Optional} de la sede encontrada, o {@link Optional#empty()} si no se encuentra.
     */
    Optional<Sede> findByNombre(String nombre);

    /**
     * Recupera todas las sedes con sus domicilios asociados, permitiendo la paginación y filtrado por nombre.
     * Si el nombre es nulo o vacío, no se aplica filtro por nombre.
     *
     * @param nombre El nombre de la sede a buscar (opcional).
     * @param pageable Objeto que contiene la información de paginación.
     * @return Un {@link Page} de {@link SedeDomicilioRecordResponse} con los resultados de la consulta.
     */
    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse(
                s.id, s.nombre, s.estado,
                new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(
                    d.id, d.calle, d.exterior, d.interior,
                    d.estadoRepublica, d.municipio, d.localidad,
                    d.colonia, d.codigoPostal, d.referencia
                ),
                s.telefono
            )
            FROM Sede s
            LEFT JOIN s.domicilio d
            WHERE (COALESCE(:nombre, '') = '' OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            """)
    Page<SedeDomicilioRecordResponse> findAllSedeDomicilioWithPagination(@Param("nombre") String nombre,
            Pageable pageable);

}