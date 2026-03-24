package mx.gob.pjpuebla.migracion.readers.juzgados;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.micrometer.common.lang.NonNull;

@Repository
public interface JuzgadosMigracionRepository extends JpaRepository<JuzgadosMigracion, Integer> {

    @Query("""
            SELECT new  mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionRegistroRecord(
                    j.id,
                    j.tablaUbicacion,
                    m.codigo,
                    j.codigo,
                    j.descripcion
                )
            FROM JuzgadosMigracion j
            JOIN j.materiaRealObj m
            WHERE j.codigo = :codigo
            """)
    Optional<JuzgadosMigracionRegistroRecord> findByCodigo(String codigo);

    @SuppressWarnings("null")
    @Override
    @NonNull
    @EntityGraph(attributePaths = { "materiaObj", "materiaRealObj" })
    List<JuzgadosMigracion> findAll();

    @Query("""
            SELECT new mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionRecord(
                j.idJuzgado,
                j.descripcion,
                j.codigo
            )
            FROM JuzgadosMigracion j
            WHERE j.codigo = :codigo

            """)
    Optional<JuzgadosMigracionRecord> findJuzgadoByCodigo(String codigo);

}