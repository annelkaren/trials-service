package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;

import mx.gob.pjpuebla.trials.litigante.responselitigante.SentenciasPublicasRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DocumentoDetalleRepository extends JpaRepository<DocumentoDetalle, Integer> {

    List<DocumentoDetalle> findAllByDocumentoId(Integer documentoId);

    List<DocumentoDetalle> findAllByDocumentoIdIn(List<Integer> documentoId);

    Optional<DocumentoDetalle> findByDocumentoId(Integer documentoId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.litigante.responselitigante.SentenciasPublicasRecord (
                doc.id,
                ca.expediente,
                juz.nombre,
                dd.tipoSentencia,
                '',
                ma.nombre,
                dd.fechaResolucion
            )
            FROM DocumentoDetalle dd
            JOIN dd.documento doc
            JOIN doc.carpeta ca
            JOIN ca.juzgado juz
            JOIN ca.tipoJuicio tj
            JOIN tj.materia ma
            WHERE doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.SENTENCIA
            AND ma.id = :materiaId
            """)
    Page<SentenciasPublicasRecord> findSentenciasByMateriaId(Integer materiaId, Pageable pageable);
}
