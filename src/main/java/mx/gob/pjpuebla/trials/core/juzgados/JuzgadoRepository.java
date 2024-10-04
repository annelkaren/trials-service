package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuzgadoRepository extends JpaRepository<Juzgado, Integer> {

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
            AND j.contadorAsignaciones = (SELECT MIN(t.contadorAsignaciones) from Juzgado t WHERE t.materia = j.materia and j.estado = t.estado)
            """)
    List<Juzgado> findJuzgadosMenosAsignaciones(Materia materia);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Juzgado j SET j.contadorAsignaciones = j.contadorAsignaciones + 1 WHERE j.id = :juzgadoId")
    void actualizarContadorAsignaciones(Integer juzgadoId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Juzgado j SET j.contadorAsignaciones = j.contadorAsignaciones - j.maxAsignacionesRonda
            WHERE j.materia = :materia
            """)
    void reiniciarContadorAsignaciones(Materia materia);

    @Query("""
            SELECT COALESCE(SUM(j.contadorAsignaciones), 0)
            FROM Juzgado j
            WHERE j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            """)
    Integer sumContadorAsignacionesByMateria(Materia materia);

    @Query("""
            SELECT COALESCE(SUM(j.maxAsignacionesRonda), 0)
            FROM Juzgado j
            WHERE j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            """)
    Integer sumMaxAsignacionesRondaByMateria(Materia materia);

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

}
