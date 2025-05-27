package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudesProrrogasRepository extends JpaRepository<SolicitudesProrrogas, Integer> {

    Optional<SolicitudesProrrogas> findFirstByMovimientoIdOrderByIdDesc(Integer movimientoId);

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas.SolicitudProrrogaRecordResponse(
                    sp.id,
                    c.expediente,
                    c.tipoCarpeta,
                    sp.motivoProrroga,
                    m.fechaAsignacion,
                    sp.audit,
                    concepto.dias,
                    sp.fechaProrroga,
                    c.estatus,
                   CONCAT(COALESCE(persona.nombre, ''), ' ', COALESCE(persona.apellidoPaterno, ''),
                    ' ',COALESCE(persona.apellidoMaterno, '')
)
                )
                FROM SolicitudesProrrogas sp
                JOIN sp.movimiento m
                JOIN m.carpeta c
                JOIN c.concepto concepto
                JOIN m.persona persona
            """)
    Page<SolicitudProrrogaRecordResponse> getAll(Pageable pageable);

}
