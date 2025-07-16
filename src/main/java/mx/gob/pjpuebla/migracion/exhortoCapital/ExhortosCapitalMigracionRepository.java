package mx.gob.pjpuebla.migracion.exhortoCapital;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExhortosCapitalMigracionRepository extends JpaRepository<ExhortosCapitalMigracion, Integer> {
    List<ExhortosCapitalMigracion> findByJuzgadoOr(String juzgadoOr);
}