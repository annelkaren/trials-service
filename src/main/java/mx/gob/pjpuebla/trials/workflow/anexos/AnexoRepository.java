package mx.gob.pjpuebla.trials.workflow.anexos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Integer> {

    List<Anexo> findAllByDocumentoId(Integer documentoId);

    @Query("""
                SELECT a.nombre
                FROM Anexo a
                WHERE a.documento.id = :documentoId
            """)
    List<String> findNombresAnexosByDocumentoId(@Param("documentoId") Integer documentoId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecepcionRecord(a.id, a.estado, a.nombre)
            FROM Anexo a
            WHERE a.documento.id = :documentoId
            """)
    List<AnexoRecepcionRecord> findAnexosByDocumentoId(@Param("documentoId") Integer documentoId);

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord(
                    a.id,
                    a.nombre,
                    a.estado
                )
                FROM Anexo a
                WHERE a.documento.id = :documentoId
                OR (:documentoId IS NULL AND a.documento.id IN (
                    SELECT docCarpeta.id FROM Documento docCarpeta
                    WHERE docCarpeta.carpeta.id = :carpetaId and docCarpeta.tipoDocumento is null
                ))
            """)
    List<AnexoBandejaRecepcionRecord> findAnexosByCarpetaIdOrDocumentoId(@Param("documentoId") Integer documentoId,
            @Param("carpetaId") Integer carpetaId);

}