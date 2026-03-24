package mx.gob.pjpuebla.migracion.readers.ocomun;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OcomunRepository extends JpaRepository<Ocomun, Integer> {

    @Query("""
                SELECT new mx.gob.pjpuebla.migracion.readers.ocomun.OcomunResponseRecord(
                    o.id,
                    o.folio,
                    o.rutaDigitalizacion,
                    o.anexos
                )
                FROM Ocomun o
                WHERE o.cu = :cu AND o.estatus = :estatus
                ORDER BY o.id DESC
            """)
    Optional<OcomunResponseRecord> findTopByCuAndEstatusOrderByIdDesc(String cu, String estatus);

}
