package mx.gob.pjpuebla.trials.core.bloques;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import mx.gob.pjpuebla.trials.core.sedes.SedeRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BloqueRepository extends JpaRepository<Bloque, Integer> {

    Page<Bloque> findByHoraInicial(LocalTime horaInicial, Pageable pageable);

    @Query("""
            SELECT 
            new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(b.id, b.horaInicial, b.horaFinal, b.estado)
            FROM Bloque b
            WHERE b.id = :id AND b.estado IN :estados""")
    Optional<BloqueRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

}
