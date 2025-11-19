package mx.gob.pjpuebla.migracion.readers.detalle;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.litigante.LitigantePromocionesLegacyProjection;

public interface DetallesMigracionRepository extends JpaRepository<DetallesMigracion, Integer> {
    
    @Query(value = """
            SELECT
                CAST(COALESCE(detalle.id, dp.id) AS SIGNED) AS id,
                CONCAT(entrada.expediente, '/', entrada.amo) AS numeroExpediente,
                CAST(COALESCE(detalle.id, dp.id) AS CHAR) AS numeroPromocionE,
                u.correo AS usuarioOrigen,
                CASE
                    WHEN COALESCE(detalle.archivo, dp.archivo) IS NULL THEN ''
                    ELSE 'Promoción'
                END AS nombreArchivo,
                COALESCE(detalle.fecha, dp.fecha) AS fechaSubida,
                COALESCE(detalle.hora, dp.hora) AS horaSubida,
                COALESCE(dp.archivo, detalle.archivo, '') AS rutaArchivo,
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
            """, nativeQuery = true)
    List<LitigantePromocionesLegacyProjection> findPromocionesElectronicasLitiganteLegacy(String correo);

}
