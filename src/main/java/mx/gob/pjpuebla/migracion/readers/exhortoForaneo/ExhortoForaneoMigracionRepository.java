package mx.gob.pjpuebla.migracion.readers.exhortoForaneo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExhortoForaneoMigracionRepository extends JpaRepository<ExhortoForaneoMigracion, Integer> {
    List<ExhortoForaneoMigracion> findByJuzgadoOr(String juzgadoOr);
}
