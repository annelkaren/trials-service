package mx.gob.pjpuebla.trials.core.distritos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistritoRepository extends JpaRepository<Distrito, Integer> {

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.core.distritos.DistritoRecord(
            d.id,
            d.nombre
        )
        FROM Distrito d
        WHERE d.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
        ORDER BY d.nombre ASC
    """)
    List<DistritoRecord> findAllForSelect();
}
