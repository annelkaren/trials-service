package mx.gob.pjpuebla.migracion.readers.entradas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.litigante.LitiganteExpedientesInterface;

@Repository
public interface EntradasMigracionRepository extends JpaRepository<EntradasMigracion, Integer> {

    @Query("""
                SELECT entrada
                FROM EntradasMigracion entrada
                LEFT JOIN JuzgadosMigracion  juzgado ON entrada.juzgado = juzgado.codigo
                WHERE entrada.expediente = :expediente AND entrada.amo = :amo AND entrada.juzgado = :juzgado
            """)
    List<EntradasMigracion> buscarPorExpedienteAmoYJuzgado(
            @Param("expediente") String expediente,
            @Param("amo") Integer amo,
            @Param("juzgado") String juzgado

    );

    @Query("""
                SELECT e
                FROM EntradasMigracion e
                WHERE COALESCE(NULLIF(TRIM(LEADING '0' FROM e.expediente), ''), '0') = :expediente
                  AND e.amo = :amo
                  AND e.juzgado = :juzgado
                  AND e.status = :status
                ORDER BY e.id DESC
            """)
    Optional<EntradasMigracion> findTopByExpedienteNormalizado(
            @Param("expediente") String expediente,
            @Param("amo") Integer amo,
            @Param("juzgado") String juzgado,
            @Param("status") String status);

    Optional<EntradasMigracion> findTopByExpedienteAndAmoAndJuzgadoAndStatusOrderByIdDesc(String expediente,
            Integer amo, String juzgado, String status);

@Query(value = """
            SELECT
                null as id,
                CONCAT(entrada.expediente, '/', entrada.amo) as numeroExpediente,
                materia.materia as materia,
                juicio.descrip as tipoJuicio,

                (SELECT nombre
                 FROM acuerdos.actores
                 WHERE clave = CONVERT(entrada.cu USING latin1)
                 AND tipo = 'A'
                 ORDER BY id
                 LIMIT 1) as actorPrincipal,

                (SELECT nombre
                 FROM acuerdos.actores
                 WHERE clave = CONVERT(entrada.cu USING latin1)
                 AND tipo = 'D'
                 ORDER BY id
                 LIMIT 1) as demandadoPrincipal,

                juzgado.descrip as juzgado,
            
                CAST(null AS SIGNED) as notificacionesPendientes,
            
                '' as sede,
                entrada.cu as cu
            
            FROM acuerdos.entradas entrada
            JOIN acuerdos.juicios juicio ON entrada.juicio = juicio.idjuicio
            JOIN acuerdos.entradasusuario entradasUsuario ON entrada.cu = entradasUsuario.cuEntradas
            JOIN acuerdos.usuario usuario ON usuario.idusuario = entradasUsuario.idUsuario
            JOIN acuerdos.juzgados juzgado ON entrada.juzgado = juzgado.codigo
            JOIN acuerdos.materias materia ON juzgado.materia = materia.codigo
            WHERE
                usuario.correo = :correo
                AND entrada.status = 'A'
                AND usuario.estatus = 'A'
                AND entradasUsuario.estatus = 'A'
                AND (
                    LOWER(juzgado.descrip) LIKE CONCAT('%', LOWER(:key), '%')
                    OR LOWER(CONCAT(entrada.expediente, '/', entrada.amo)) LIKE CONCAT('%', LOWER(:key), '%')
              )
            """, 
            nativeQuery = true)
    List<LitiganteExpedientesInterface> findExpedientesRelacionadosLegacy(String correo, String key);

    @Query("SELECT e FROM EntradasMigracion e WHERE e.cu = :cu and e.status = 'A' order by e.id desc limit 1")
    Optional<EntradasMigracion> findByCu(String cu);
}