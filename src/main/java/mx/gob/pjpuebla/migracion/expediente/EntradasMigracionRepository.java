package mx.gob.pjpuebla.migracion.expediente;

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

    Optional<EntradasMigracion> findTopByExpedienteAndAmoAndJuzgadoAndStatusOrderByIdDesc(String expediente, Integer amo, String juzgado, String status);
}