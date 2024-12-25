package mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarpetaEtapasRepository extends JpaRepository<CarpetaEtapas, Integer> {

    @Query("""
        SELECT ce
        FROM CarpetaEtapas ce
        WHERE fechaRegistro = (
            SELECT MAX(ce2.fechaRegistro)
            FROM CarpetaEtapas ce2
            WHERE ce2.carpeta.id = ce.carpeta.id
        )
        AND ce.carpeta.id = :carpetaId
    """)
    Optional<CarpetaEtapas> findByCarpetaId(Integer carpetaId);


}
