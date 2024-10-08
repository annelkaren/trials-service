package mx.gob.pjpuebla.trials.workflow.carpeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
            SELECT 
                new mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord(
                    d.id,
                    c.folio, 
                    c.expediente,
                    c.tipoCarpeta,
                    d.ruta,
                   null 
                )
            FROM Documento d
            JOIN d.carpeta c
            JOIN c.juzgado j
            WHERE c.folio = :folio AND j.nombre = :nombre
        """)
    BandejaRecepcionRecord findByFolioAndJuzgado_Name(String folio, String nombre);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord(
            a.id, a.nombre, 0)
        FROM Anexo a
        JOIN a.documento d
        WHERE d.id = :documentoId
    """)
    List<AnexoBandejaRecepcionRecord> findAnexosByDocumentoId(Integer documentoId);

}
