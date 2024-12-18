package mx.gob.pjpuebla.trials.core.religiones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReligionesRepository  extends JpaRepository<Religiones, Integer>{

    @Query("""
        SELECT
        new mx.gob.pjpuebla.trials.core.religiones.ReligionesRecord(r.id, r.nombre)
        FROM Religiones r
        WHERE (
            :key IS NULL OR
            LOWER(TRANSLATE(r.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :key, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN'))
        )
        """)
    List<ReligionesRecord> findAllByidReligion(
            @Param("key") String key
    );

}
