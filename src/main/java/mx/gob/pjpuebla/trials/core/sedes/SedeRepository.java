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
 * Proporciona métodos para interactuar con la base de datos relacionados con
 * las sedes y sus domicilios asociados.
 */
@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {

    /**
     * Recupera una sede por su ID y su estado, devolviendo un {@link SedeRecord}
     * con los datos completos.
     * Si no se encuentra la sede, devuelve un {@link Optional#empty()}.
     *
     * @param id      El ID de la sede a buscar.
     * @param estados Una lista de estados para filtrar las sedes activas o
     *                inactivas.
     * @return Un {@link Optional} con el {@link SedeRecord} de la sede encontrada,
     *         o {@link Optional#empty()} si no se encuentra.
     */
    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord(s.id, s.version, s.nombre, s.estado, s.tipo, s.telefono, s.extension,
            new mx.gob.pjpuebla.trials.core.distritos.DistritoRecord(dis.id, dis.nombre),
            new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(dom.id, dom.calle, dom.exterior,
                dom.interior, dom.estadoRepublica, dom.municipio, dom.localidad, dom.colonia, dom.codigoPostal, dom.referencia, dom.ciudad),
            s.latitude, s.longitude, s.photo)
            FROM Sede s
            LEFT JOIN s.domicilio dom
            LEFT JOIN s.distrito dis
            WHERE s.id =:id AND s.estado IN :estados
            """)
    Optional<SedeRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    /**
     * Recupera todas las sedes junto con sus domicilios asociados, permitiendo la
     * paginación de los resultados.
     *
     * @param pageable Objeto que contiene la información de paginación.
     * @return Un {@link Page} de {@link SedeDomiciliosRecord} con los resultados de
     *         la consulta.
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
     * @return Un {@link Optional} de la sede encontrada, o {@link Optional#empty()}
     *         si no se encuentra.
     */
    Optional<Sede> findByNombre(String nombre);

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse(
                    s.id, s.nombre, s.estado,
                    new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(
                        d.id, d.calle, d.exterior, d.interior,
                        d.estadoRepublica, d.municipio, d.localidad,
                        d.colonia, d.codigoPostal, d.referencia, d.ciudad
                    ),
                    s.telefono
                )
                FROM Sede s
                LEFT JOIN s.domicilio d
                WHERE (:key = ''
                    OR LOWER(COALESCE(s.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                    OR LOWER(COALESCE(d.calle, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                    OR LOWER(COALESCE(d.exterior, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                    OR LOWER(COALESCE(d.colonia, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                    OR LOWER(COALESCE(d.estadoRepublica, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                    OR LOWER(COALESCE(d.codigoPostal, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                    OR LOWER(COALESCE(s.telefono, '')) LIKE LOWER(CONCAT('%', :key, '%')))
                  AND (s.nombre IS NULL OR s.nombre LIKE CONCAT('%', :nombre, '%'))
                  AND (d.calle IS NULL OR d.calle LIKE CONCAT('%', :direccion, '%'))
                  AND (s.telefono IS NULL OR s.telefono LIKE CONCAT('%', :telefono, '%'))
                  AND s.estado IN :estados
            """)
    Page<SedeDomicilioRecordResponse> findAllSedeDomicilioWithPagination(
            @Param("key") String key,
            @Param("nombre") String nombre,
            @Param("direccion") String direccion,
            @Param("telefono") String telefono,
            @Param("estados") List<Estado> estados,
            Pageable pageable);

}