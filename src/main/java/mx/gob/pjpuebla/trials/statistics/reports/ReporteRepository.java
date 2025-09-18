package mx.gob.pjpuebla.trials.statistics.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

}
