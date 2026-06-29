package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OficioCentralComisarioRepository extends JpaRepository<OficioCentralComisario, Integer> {

    Optional<OficioCentralComisario> findByDocumentoId(Integer oficioId);

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.workflow.centralcomisarios.OficioEnviado(
                    doc.id,
                    COALESCE(c.expediente, 'N/A'),
                    doc.folio,
                    ins.nombre,
                    dd.asunto,
                    occ.estado,
                    dd.fechaEmision,
                    dd.fechaEntrega,
                    occ.comisario.id
                )
                FROM OficioCentralComisario occ
                JOIN occ.documento doc
                LEFT JOIN doc.institucion ins
                LEFT JOIN DocumentoDetalle dd ON dd.documento = doc
                LEFT JOIN doc.carpeta c
                WHERE doc.tipoDocumento =  mx.gob.pjpuebla.trials.util.enums.TipoDocumento.OFICIO
                AND occ.comisario.id = :comisarioId
            """)
    Page<OficioEnviado> findDocumentosByComisario(@Param("comisarioId") Long comisarioId, Pageable pageable);
}
