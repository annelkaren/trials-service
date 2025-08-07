package mx.gob.pjpuebla.trials.statistics.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, ReporteId> {

    @Query("""
            SELECT
            DISTINCT new mx.gob.pjpuebla.trials.statistics.reports.ReporteDateRecord(r.reportKey,  r.extraData)
            FROM Reporte r
            WHERE r.extraData is not null""")
    List<ReporteDateRecord> getAllKeys();
}
