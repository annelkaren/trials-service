package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer>, SecuenciaRepositoryCustom {

    @Query(value = "SELECT doc FROM Documento doc "
            + "JOIN FETCH doc.carpeta c "
            + "JOIN FETCH c.juzgado j "
            + "JOIN FETCH j.materia m "
            + "WHERE c.estatus = mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.CAPTURA"
            + "AND (lower(m.nombre) LIKE %:key% OR lower(c.folio) LIKE %:key% OR lower(c.expediente) LIKE %:key%) "
            + "ORDER BY doc.audit.fechaAlta ASC")
    Page<Documento> findByEstatusCaptura(String key, Pageable pageable);
}
