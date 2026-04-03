package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptoRepository extends JpaRepository<Concepto, Integer> {
    Optional<Concepto> findByNombre(String nombre);

    Optional<Concepto> findByNombreAndTipoJuicio(String nombre, TipoJuicio tipoJuicio);

    List<Concepto> findAllByTipoJuicio_IdOrNombreIn(Integer tipoJuicioId, List<String> nombres);

    @Query(value = "SELECT c FROM Concepto c "
            + "LEFT JOIN FETCH c.tipoJuicio tj "
            + "WHERE c.estado IN :estados "
            + "AND (:key = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :key, '%')) "
            + "     OR CAST(c.dias AS string) LIKE CONCAT('%', :key, '%') "
            + "     OR LOWER(tj.nombre) LIKE LOWER(CONCAT('%', :key, '%'))) "
            + "AND (:nombre = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) "
            + "AND (:dias IS NULL OR c.dias = :dias) "
            + "AND (:nombreTipoJuicio = '' OR LOWER(tj.nombre) LIKE LOWER(CONCAT('%', :nombreTipoJuicio, '%')))")
    Page<Concepto> findAllConceptos(String key, List<Estado> estados, Pageable pageable, String nombre, Integer dias,
            String nombreTipoJuicio);
}
