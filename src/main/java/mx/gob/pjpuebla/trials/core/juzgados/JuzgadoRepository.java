package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import mx.gob.pjpuebla.trials.core.materias.Materia;

@Repository
public interface JuzgadoRepository extends JpaRepository<Juzgado, Integer>, JuzgadoRepositoryCustom {

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecord(f.id, f.version, f.nombre, f.estado, m.id, s.id, f.maxAsignacionesRonda, f.contadorAsignaciones)
            FROM Juzgado f
            LEFT JOIN f.materia m
            LEFT JOIN f.sede s
            WHERE f.id =:id AND f.estado IN :estados""")
    Optional<JuzgadoRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordResponse(f.id,  f.nombre, f.estado, m.nombre, f.maxAsignacionesRonda, f.contadorAsignaciones)
            FROM Juzgado f
            LEFT JOIN f.materia m
            WHERE f.estado IN :estados""")
    List<JuzgadoRecordResponse> findAllByEstadoIn(List<Estado> estados);

    List<Juzgado> findByMateriaAndEstado(Materia materia, Estado estado);

    @Query("""
            SELECT j FROM Juzgado j 
            WHERE j.contadorAsignaciones < j.maxAsignacionesRonda 
            AND j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND j.contadorAsignaciones = (SELECT MIN(t.contadorAsignaciones) from Juzgado t WHERE t.materia = j.materia and j.estado = t.estado)
            """)
    public List<Juzgado> findJuzgadosMenosAsignaciones(Materia materia);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Juzgado j SET j.contadorAsignaciones = j.contadorAsignaciones + 1 WHERE j.id = :juzgadoId")
    public int actualizarContadorAsignaciones(Integer juzgadoId);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE Juzgado j SET j.contadorAsignaciones = j.contadorAsignaciones - j.maxAsignacionesRonda 
            WHERE j.materia = :materia
                        """)
    public int reiniciarContadorAsignaciones(Materia materia);

    @Query("SELECT SUM(j.contadorAsignaciones) FROM Juzgado j where j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE ")
    public Integer sumContadorAsignacionesByMateria(Materia materia);

    @Query("SELECT SUM(j.maxAsignacionesRonda) FROM Juzgado j where j.materia = :materia AND j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE")
    public Integer sumMaxAsignacionesRondaByMateria(Materia materia);

}
