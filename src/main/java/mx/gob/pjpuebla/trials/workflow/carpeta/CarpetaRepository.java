package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.transaction.Transactional;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecordResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarpetaRepository extends JpaRepository<Carpeta, Integer> {

    @Query("""
            SELECT c
            FROM Carpeta c
            WHERE c.expediente = :expediente
            AND c.juzgado.id = :juzgadoId
            """)
    Optional<Carpeta> findByExpedienteAndJuzgadoId(String expediente, Integer juzgadoId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord(
                    d.id,
                    c.folio,
                    c.expediente,
                    c.tipoCarpeta,
                    d.ruta,
                   null
                )
            FROM Documento d
            JOIN d.carpeta c
            WHERE d.id = :documentoId
        """)
    BandejaRecepcionRecord findByDocumentoId(Integer documentoId);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord(
            a.id, a.nombre, a.estado)
        FROM Anexo a
        JOIN a.documento d
        WHERE d.id = :documentoId
    """)
    List<AnexoBandejaRecepcionRecord> findAnexosByDocumentoId(Integer documentoId);

    @Transactional
    @Modifying
    @Query("UPDATE Carpeta c SET c.estatus = :estado WHERE c.id = :carpetaId")
    void actualizarEstatus(@Param("carpetaId") Integer carpetaId, @Param("estado") EstadoCarpeta estado);


    @Query("""
            SELECT COUNT(1)+1 FROM Carpeta c
            WHERE c.carpetaPadre.id=:carpetaId and c.tipoCarpeta=mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA and c.expediente like %:clavePieza%
            """)
    Integer getNumeroPieza(Integer carpetaId, String clavePieza);
    
    @Query("""
            SELECT c.tipoJuicio.id
            FROM Carpeta c
            WHERE c.id = :carpetaId
            """)
    Integer findTipoJuicioIdByCarpetaId(@Param("carpetaId") Integer carpetaId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecordResponse(
                c.id, c.expediente, tp.tipo
            )
            FROM Documento d
            JOIN Carpeta c on c.carpetaPadre.id = d.carpeta.id
            JOIN c.tipoPieza tp
            WHERE d.id = :documentoId
            AND c.tipoCarpeta=mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA
            AND c.estatus NOT IN (
                mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.CANCELADO,
                mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.INTEGRADO)
            """)
    List<PiezaRecordResponse> findPiezasByDocumentoId(Integer documentoId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.DocumentoDetalleCarpeta(
                c.id,
                c.expediente,
                null,
                c.tipoPieza,
                c.audit.fechaAlta,
                null,
                c.personaAsignada
            )
            FROM Carpeta c
            JOIN c.tipoPieza tp
            WHERE on c.carpetaPadre.id = :carpetaPadreId
            AND c.tipoCarpeta=mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA
            AND c.estatus NOT IN (
                mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.CANCELADO,
                mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.INTEGRADO)
            """)
    List<PiezaRecordResponse> findPiezasByCarpetaPadreId(Integer carpetaPadreId);
}
