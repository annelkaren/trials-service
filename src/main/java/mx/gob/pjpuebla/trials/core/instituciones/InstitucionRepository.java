package mx.gob.pjpuebla.trials.core.instituciones;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecordResponse;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Repository
public interface InstitucionRepository extends JpaRepository<Institucion, Integer> {

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecordResponse(
                    i.id,
                    i.version,
                    i.nombre,
                    i.estado,
                    i.telefono,
                    i.extension,
                    i.tipoInstitucion,
                    new mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord(
                        d.id,
                        d.calle,
                        d.exterior,
                        d.interior,
                        d.estadoRepublica,
                        d.municipio,
                        d.localidad,
                        d.colonia,
                        d.codigoPostal,
                        d.referencia,
                        d.ciudad
                    )
                )
                FROM Institucion i
                JOIN i.domicilio d
                WHERE i.id = :id AND i.estado IN :estados
            """)
    Optional<InstitucionRecordResponse> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    @Query("""
            SELECT
                new mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord(
                    i.id,
                    i.nombre,
                    CONCAT(
                        d.calle, ' ',
                        d.colonia, ' ',
                        d.exterior,
                        CASE WHEN d.interior IS NOT NULL THEN CONCAT(' Int. ', d.interior) ELSE '' END,
                        ' ',
                        d.estadoRepublica, ' ',
                        d.municipio, ' ',
                        d.localidad, ' ',
                        d.codigoPostal,
                        CASE WHEN d.referencia IS NOT NULL THEN CONCAT(' Ref: ', d.referencia) ELSE '' END
                    ),
                    i.telefono,
                    i.tipoInstitucion,
                    i.estado
                )
            FROM Institucion i
            JOIN i.domicilio d
            WHERE i.estado IN :estados
            """)
    Page<InstitucionRecord> findAllEstadoIn(@Param("estados") List<Estado> estados, Pageable pageable);

    Optional<Institucion> findByNombre(String nombre);

    @Query("""
            SELECT
                new mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord(
                    i.id,
                    i.nombre,
                    CONCAT(
                        d.calle, ' ',
                        d.colonia, ' ',
                        d.exterior,
                        CASE WHEN d.interior IS NOT NULL THEN CONCAT(' Int. ', d.interior) ELSE '' END,
                        ' ',
                        d.estadoRepublica, ' ',
                        d.municipio, ' ',
                        d.localidad, ' ',
                        d.codigoPostal,
                        CASE WHEN d.referencia IS NOT NULL THEN CONCAT(' Ref: ', d.referencia) ELSE '' END
                    ),
                    i.telefono,
                    i.tipoInstitucion,
                    i.estado
                )
            FROM Institucion i
            JOIN i.domicilio d
            WHERE i.estado = Estado.ACTIVE
            AND i.tipoInstitucion = :tipo
            """)
    List<InstitucionRecord> findByTipoInstitucion(@Param("tipo") String tipo);

    @Query("""
            SELECT
                new mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord(
                i.id,
                i.nombre,
                d,
                i.telefono,
                i.tipoInstitucion,
                i.estado
                )
            FROM Institucion i
            JOIN i.domicilio d
            WHERE :key = :key
            AND (COALESCE(:nombre, '') = '' OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            AND (COALESCE(:direccion, '') = '' OR LOWER(d.calle) LIKE LOWER(CONCAT('%', :direccion, '%')))
            AND (COALESCE(:telefono, '') = '' OR LOWER(i.telefono) LIKE LOWER(CONCAT('%', :telefono, '%')))
            AND i.estado IN :estados
            """)
    Page<InstitucionRecord> findAllInstituciones(
            @Param("key") String key,
            @Param("nombre") String nombre,
            @Param("direccion") String direccion,
            @Param("telefono") String telefono,
            @Param("estados") List<Estado> estados,
            Pageable pageable);

    @Query("""
            SELECT
                new mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord(
                    i.id,
                    i.nombre,
                    CONCAT(
                        d.calle, ' ',
                        d.colonia, ' ',
                        d.exterior,
                        CASE WHEN d.interior IS NOT NULL THEN CONCAT(' Int. ', d.interior) ELSE '' END,
                        ' ',
                        d.estadoRepublica, ' ',
                        d.municipio, ' ',
                        d.localidad, ' ',
                        d.codigoPostal,
                        CASE WHEN d.referencia IS NOT NULL THEN CONCAT(' Ref: ', d.referencia) ELSE '' END
                    ),
                    i.telefono,
                    i.tipoInstitucion,
                    i.estado
                )
            FROM Institucion i
            JOIN i.domicilio d
            WHERE i.estado = Estado.ACTIVE
            ORDER BY i.nombre
            """)
    List<InstitucionRecord> getInstitucionesList();

}