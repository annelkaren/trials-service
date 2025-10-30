package mx.gob.pjpuebla.migracion.readers.entradas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}