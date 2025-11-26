package mx.gob.pjpuebla.trials.core.eventos;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

        @Query("""
                SELECT COUNT(e) > 0 from Evento e
                where e.diaInicio <= :fecha and e.diaFin >= :fecha
                and e.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
                and ( case when e.juzgado is null then 1 when e.juzgado = :juzgado then 1 else 0 end = 1
                or case when e.oficialia is null and e.juzgado = :juzgado then 1
                when e.oficialia = :oficialia then 1 else 0 end = 1 )
                """)
        Boolean existsEventoEntreDiaInicioAndDiaFin(LocalDate fecha, Juzgado juzgado, Oficialia oficialia);
    

    @Query("""
            SELECT e from Evento e
            where e.diaInicio >= :fecha and e.diaFin <= :fecha
            and e.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            and ( case when e.juzgado is null then 1 when e.juzgado = :juzgado then 1 else 0 end = 1
            or case when e.oficialia is null then 1 when e.oficialia = :oficialia then 1 else 0 end = 1 )
            """)
    Optional<Evento> findEntreDiaInicioAndDiaFin(LocalDate fecha, Juzgado juzgado, Oficialia oficialia);

    @Query("""
        SELECT e FROM Evento e
        WHERE e.oficialia IS NULL AND e.juzgado IS NULL
        """)
    Page<Evento> findEventosGenerales(Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE (:oficialia IS NULL OR e.oficialia = :oficialia) AND (:juzgado IS NULL OR e.juzgado = :juzgado)")
    Page<Evento> findByOficialiaOrJuzgado(@Param("oficialia") Oficialia oficialia, @Param("juzgado") Juzgado juzgado, Pageable pageable);

    @Query(
            value = """
            SELECT COALESCE(SUM(t_dia_fin - t_dia_inicio + 1), 0) AS total_dias_inhabiles
            FROM trials.tbl_eventos
            WHERE fn_juzgado IS NULL
              AND fn_oficialia IS NULL
              AND EXTRACT(YEAR FROM t_dia_inicio) = EXTRACT(YEAR FROM CURRENT_DATE)
            """,
            nativeQuery = true
    )
    Integer findTotalDiasInhabilesAnioActual();

}
