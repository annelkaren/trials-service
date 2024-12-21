package mx.gob.pjpuebla.trials.core.materiapericial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaPericialRepository extends JpaRepository<MateriaPericial, Integer> {

    @Query("""
            SELECT c
            FROM MateriaPericial c
            WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
            """)
    List<MateriaPericial> getAllMateriaPericial(@Param("nombre") String nombre);

}