package mx.gob.pjpuebla.trials.workflow.contadoresJuzgados;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Repository
public interface ContadorJuzgadoRepository extends JpaRepository<ContadorJuzgado, Integer> {

    Optional<ContadorJuzgado> findByJuzgadoIdAndTipoJuicioId(Integer juzgadoId, Integer tipoJuicioId);
    List<ContadorJuzgado> findByJuzgadoIdAndEstado(Integer juzgadoId, Estado estado);
    Optional<ContadorJuzgado> findByIdAndEstadoIn(Integer id, List<Estado> estados);
    Page<ContadorJuzgado> findByEstado(Estado estado, Pageable pageable);

    @Query("""
            SELECT juzgado
            FROM ContadorJuzgado c
            JOIN c.juzgado juzgado
            WHERE c.contadorAsignaciones < c.maxAsignaciones
            AND juzgado.id IN :juzgadosIds
            AND c.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND c.tipoJuicio.id = :tipoJuicioId
            """)
    List<Juzgado> findJuzgadosMenosAsignaciones(@Param("juzgadosIds") List<Integer> juzgadosIds,
                                                @Param("tipoJuicioId") Integer tipoJuicioId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE ContadorJuzgado c
            SET c.contadorAsignaciones = c.contadorAsignaciones + 1
            WHERE c.juzgado.id = :juzgadoId
            AND c.tipoJuicio.id = :tipoJuicioId
            AND c.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            """)
    int actualizarContadorAsignaciones(@Param("juzgadoId") Integer juzgadoId,
                                       @Param("tipoJuicioId") Integer tipoJuicioId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE ContadorJuzgado c
            SET c.contadorAsignaciones = c.contadorAsignaciones - c.maxAsignaciones
            WHERE c.juzgado.id IN :juzgadosIds
            AND c.tipoJuicio.id = :tipoJuicioId
            AND c.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            """)
    void reiniciarContadorAsignaciones(@Param("juzgadosIds") List<Integer> juzgadosIds,
                                       @Param("tipoJuicioId") Integer tipoJuicioId);

    @Query("""
            SELECT COALESCE(SUM(c.contadorAsignaciones), 0)
            FROM ContadorJuzgado c
            WHERE c.juzgado.id IN :juzgadosIds
            AND c.tipoJuicio.id = :tipoJuicioId
            AND c.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            """)
    Integer sumContadorAsignaciones(@Param("juzgadosIds") List<Integer> juzgadosIds,
                                    @Param("tipoJuicioId") Integer tipoJuicioId);

    @Query("""
            SELECT COALESCE(SUM(c.maxAsignaciones), 0)
            FROM ContadorJuzgado c
            WHERE c.juzgado.id IN :juzgadosIds
            AND c.tipoJuicio.id = :tipoJuicioId
            AND c.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            """)
    Integer sumMaxAsignaciones(@Param("juzgadosIds") List<Integer> juzgadosIds,
                               @Param("tipoJuicioId") Integer tipoJuicioId);
}
