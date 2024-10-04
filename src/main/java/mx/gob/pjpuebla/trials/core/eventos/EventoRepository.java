package mx.gob.pjpuebla.trials.core.eventos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    @Query("""
            SELECT 1 from Evento e
            where e.diaInicio >= :fecha and e.diaFin <= :fecha
            """)
    Boolean existsFechaEntreDiaInicioAndDiaFin(LocalDate fecha);


    @Query("""
            SELECT e from Evento e
            where e.diaInicio >= :fecha and e.diaFin <= :fecha
            """)
    Optional<Evento> findFechaEntreDiaInicioAndDiaFin(LocalDate fecha);
}
