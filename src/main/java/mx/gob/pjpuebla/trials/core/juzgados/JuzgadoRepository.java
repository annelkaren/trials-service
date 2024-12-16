package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuzgadoRepository extends JpaRepository<Juzgado, Integer> {

    @Query(value = "SELECT j FROM Juzgado j "
            + "JOIN FETCH j.materia m "
            + "WHERE j.estado IN :estados "
            + "AND (lower(j.nombre) LIKE %:key% OR lower(m.nombre) LIKE %:key%)")
    Page<Juzgado> findAll(String key, List<Estado> estados, Pageable pageable);

    Optional<Juzgado> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(f.id,  f.nombre, f.estado, m.nombre)
            FROM Juzgado f
            LEFT JOIN f.materia m
            WHERE f.estado IN :estados""")
    List<JuzgadoRecordItem> findAllByEstadoIn(List<Estado> estados);

    List<Juzgado> findByMateriaAndEstado(Materia materia, Estado estado);

    @Query("""
            SELECT j FROM Juzgado j
            WHERE j.contadorAsignaciones < j.maxAsignacionesRonda
            AND j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND j.instanciaJuzgado = :instanciaJuzgado
            """)
    List<Juzgado> findJuzgadosMenosAsignaciones(Materia materia, InstanciaJuzgado instanciaJuzgado);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Juzgado j SET j.contadorAsignaciones = j.contadorAsignaciones + 1 WHERE j.id = :juzgadoId")
    void actualizarContadorAsignaciones(Integer juzgadoId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Juzgado j SET j.contadorAsignaciones = j.contadorAsignaciones - j.maxAsignacionesRonda
            WHERE j.materia = :materia AND j.instanciaJuzgado = :instanciaJuzgado
            """)
    void reiniciarContadorAsignaciones(Materia materia,  InstanciaJuzgado instanciaJuzgado);

    @Query("""
            SELECT COALESCE(SUM(j.contadorAsignaciones), 0)
            FROM Juzgado j
            WHERE j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND j.instanciaJuzgado = :instanciaJuzgado
            """)
    Integer sumContadorAsignacionesByMateria(Materia materia, InstanciaJuzgado instanciaJuzgado);

    @Query("""
            SELECT COALESCE(SUM(j.maxAsignacionesRonda), 0)
            FROM Juzgado j
            WHERE j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND j.instanciaJuzgado = :instanciaJuzgado
            """)
    Integer sumMaxAsignacionesRondaByMateria(Materia materia,  InstanciaJuzgado instanciaJuzgado);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoTipoJuiciosRecord (
            juz.id,
            j.nombre
        )
        FROM Juzgado juz
        JOIN juz.tipoJuicios j
        WHERE juz.id = :juzgadoId
        """)
    List<JuzgadoTipoJuiciosRecord> findTipoJuiciosByJuzgadoId(
            @Param("juzgadoId") Integer juzgadoId);
    
    @Query("SELECT j FROM OficialiaJuzgado oj JOIN Juzgado j ON oj.juzgadoId = j.id WHERE oj.oficialiaId = :oficialiaId AND j.estado IN :estados")
    List<Juzgado> findByOficialiaIdAndEstadoIn(@Param("oficialiaId") Integer oficialiaId, @Param("estados") List<Estado> estados);

    @Query("""
        SELECT
        new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(f.id, f.nombre, f.estado, m.nombre)
        FROM Juzgado f
        LEFT JOIN f.materia m
        WHERE f.estado = :estado
        AND (lower(f.nombre) LIKE %:key% OR lower(m.nombre) LIKE %:key%)
        AND (
                (:centroTrabajo IS NULL AND :idCentroTrabajo IS NULL)
             OR
                ( :centroTrabajo = "Juzgado" AND f.id = :idCentroTrabajo)
             OR ( :centroTrabajo = "Oficialia" AND f.id IN (
                    SELECT oj.id FROM Oficialia o
                    JOIN o.juzgados oj
                    WHERE o.id = :idCentroTrabajo
                ))
            )
        """)
    List<JuzgadoRecordItem> findAllByEstadoAutocomplete(
            @Param("estado") Estado estado,
            @Param("key") String key,
            @Param("centroTrabajo") String centroTrabajo,
            @Param("idCentroTrabajo" ) Integer idCentroTrabajo
    );

    @Query("""
        SELECT
        new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(f.id, f.nombre, f.estado, m.nombre)
        FROM Juzgado f
        LEFT JOIN f.materia m
        WHERE f.estado = :estado
        AND (lower(f.nombre) LIKE %:key% OR lower(m.nombre) LIKE %:key%)
        """)
    List<JuzgadoRecordItem> findAllByEstadoAutocomplete(
            @Param("estado") Estado estado,
            @Param("key") String key
    );
    

    Optional<Juzgado> findByNombreIgnoreCase(String nombre);

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(f.id,  f.nombre, f.estado, m.nombre)
            FROM Juzgado f
            LEFT JOIN f.materia m
            WHERE f.instanciaJuzgado=:instanciaJuzgado
            """)
    List<JuzgadoRecordItem> findAllByInstancia(InstanciaJuzgado instanciaJuzgado);

    @Query("SELECT oj.juzgado FROM OficialiaJuzgado oj WHERE oj.oficialiaId = :oficialiaId")
    List<Juzgado> findJuzgadoByOficialiaId(@Param("oficialiaId") Integer oficialiaId);
}
