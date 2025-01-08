package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;

import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.DetallesPruebasRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienciaPruebasRepository extends JpaRepository<AudienciaPruebas, Long>{

    @Query("""
            SELECT new  mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.DetallesPruebasRecord(
                ap.id,
                ap.nombreDeclarante,
                tp.nombre,
                ap.descripcionInstrumento,
                mp.nombre,
                ap.desistimientoAdmision
            ) FROM AudienciaPruebas ap
            JOIN ap.audiencia a
            LEFT JOIN ap.tipoPrueba tp
            LEFT JOIN ap.materiaPericial mp
            WHERE a.id = :audiencia
            """)
    Page<DetallesPruebasRecord> getAllByAudiencia(@Param("audiencia") Integer audiencia, Pageable pageable);


}
