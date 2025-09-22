package mx.gob.pjpuebla.migracion.readers.exhortoCapital;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExhortosCapitalMigracionRepository extends JpaRepository<ExhortosCapitalMigracion, Integer> {
    List<ExhortosCapitalMigracion> findByJuzgado(String juzgado);

    List<ExhortosCapitalMigracion> findByNumeroAndAmoAndJuzgado(String expediente, Integer amo, String juzgado);
}