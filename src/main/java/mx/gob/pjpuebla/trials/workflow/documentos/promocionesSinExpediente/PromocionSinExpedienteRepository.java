package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedientePageRecord;

@Repository
public interface PromocionSinExpedienteRepository extends JpaRepository<PromocionSinExpediente, Integer> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedientePageRecord(
                p.id,
                p.folio,
                p.expediente,
                j.nombre,
                t.nombre,
                p.estado
            )
            FROM PromocionSinExpediente p
            JOIN p.juzgado j
            LEFT JOIN p.tipoJuicio t
            WHERE j in (:juzgados)
            """)
    Page<PromocionSinExpedientePageRecord> getAll(Pageable pageable, List<Juzgado> juzgados);

    Optional<PromocionSinExpediente> findByCarpetaId(Integer carpetaId);

    Optional<PromocionSinExpediente> findByDocumentoId(Integer documentoId);
}
