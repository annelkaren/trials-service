package mx.gob.pjpuebla.trials.core.nacionalidades;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NacionalidadRepository extends JpaRepository<Nacionalidad, Integer> {

    @Query("""
            SELECT n
            FROM Nacionalidad n
            WHERE (lower(n.name) LIKE %:key%)
            ORDER BY n.name
            """)
    List<Nacionalidad> findAllFilterByName(String key);
}
