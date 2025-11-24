package mx.gob.pjpuebla.migracion.readers.detalle;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.gob.pjpuebla.trials.litigante.LitigantePromocionesLegacyProjection;
import mx.gob.pjpuebla.trials.workflow.documentos.records.AcusePromocionDetailRecord;

public interface DetallesMigracionRepository extends JpaRepository<DetallesMigracion, Integer> {

    @Query(value = """
            SELECT
                dp.id AS id,
                CONCAT(entrada.expediente, '/', entrada.amo) AS numeroExpediente,
                CAST(COALESCE(detalle.id, dp.id) AS CHAR) AS numeroPromocionE,
                u.correo AS usuarioOrigen,
                CASE
                    WHEN COALESCE(detalle.archivo, dp.archivo) IS NULL THEN ''
                    ELSE 'Promoción'
                END AS nombreArchivo,
                COALESCE(detalle.fecha, dp.fecha) AS fechaSubida,
                COALESCE(detalle.hora, dp.hora) AS horaSubida,
                dp.archivo AS rutaArchivo,
                juzgado.descrip AS juzgado
            FROM acuerdos.entradas entrada
            JOIN acuerdos.entradasusuario eu
                ON eu.cuEntradas = entrada.cu
                AND entrada.status = 'A'
                AND eu.estatus = 'A'
            JOIN acuerdos.juzgados juzgado
                ON juzgado.codigo = entrada.juzgado
            LEFT JOIN acuerdos.detalles detalle
                ON detalle.cu = CONVERT(entrada.cu USING latin1)
                AND detalle.status = 'R'
            LEFT JOIN acuerdos.detalles_prom dp
                ON dp.descrip = 'PROMOCION ELECTRONICA'
                AND dp.cu = CONVERT(entrada.cu USING latin1)
                AND dp.status = 'A'
            JOIN acuerdos.usuario u
                ON u.idusuario = eu.idusuario
                AND u.estatus = 'A'
            WHERE u.correo = :correo
              AND COALESCE(detalle.id, dp.id) IS NOT NULL
              AND (
                    LOWER(juzgado.descrip) LIKE CONCAT('%', LOWER(:key), '%')
                    OR LOWER(CONCAT(entrada.expediente, '/', entrada.amo)) LIKE CONCAT('%', LOWER(:key), '%')
              )
            """, nativeQuery = true)
    List<LitigantePromocionesLegacyProjection> findPromocionesElectronicasLitiganteLegacy(
            @Param("correo") String correo,
            @Param("key") String key);

    
    @Query("""
            SELECT new.mx.gob.pjpuebla.trials.workflow.documentos.records.AcusePromocionDetailRecord(
            juzgado.descripcion,
            concat(entrada.expediente, '/', entrada.amo),
            detalle.id,
            detalle.fecha,
            detalle.hora,
            detalle.fechaRec,
            detalle.horaRec,
            detalle.referencia,
            'OFICIAL MAYOR DE JUZGADO',
            detalle.anexos,
            tp.nombre
            )
            FROM DetallesMigracion detalle
            LEFT JOIN DetallesProm dp on  detalle.id = dp.referencia and dp.descrip = 'PROMOCION ELECTRONICA' AND dp.status = 'A'
            JOIN EntradasMigracion entrada on entrada.cu  = detalle.cu 
            JOIN JuzgadosMigracion juzgado on juzgado.codigo = entrada.juzgado
            JOIN TipoPromocionesMigracion tp on tp.id = detalle.tipo
            where detalle.id = :id
            """)
    Optional<AcusePromocionDetailRecord> findDetalleAcusePromocionById(Integer id);

}