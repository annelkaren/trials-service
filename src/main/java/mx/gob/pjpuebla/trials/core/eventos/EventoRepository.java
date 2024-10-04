package mx.gob.pjpuebla.trials.core.eventos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;

import java.time.LocalDate;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    @Query("""
            SELECT COUNT(e) > 0 from Evento e
            where e.diaInicio >= :fecha and e.diaFin <= :fecha
            and e.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            and ( case when e.juzgado is null then 1 when e.juzgado = :juzgado then 1 else 0 end = 1
            or case when e.oficialia is null then 1 when e.oficialia = :oficialia then 1 else 0 end = 1 )
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
}
