package mx.gob.pjpuebla.trials.core.lenguasindigenas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LenguaIndigenaRepository extends JpaRepository<LenguaIndigena, Integer> {

    @Query("""
            SELECT l
            FROM LenguaIndigena l
            WHERE (lower(l.name) LIKE %:key%)
            """)
    List<LenguaIndigena> findAllFilterByName(String key);
}
