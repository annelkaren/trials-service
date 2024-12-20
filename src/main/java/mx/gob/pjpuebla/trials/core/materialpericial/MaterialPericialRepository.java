package mx.gob.pjpuebla.trials.core.materialpericial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialPericialRepository extends JpaRepository<MaterialPericial, Integer> {

    @Query("""
            SELECT c
            FROM MaterialPericial c
            WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
            """)
    List<MaterialPericial> getAllMateriaPericial(@Param("nombre") String nombre);

}