package mx.gob.pjpuebla.migracion.readers.juzgados;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.micrometer.common.lang.NonNull;


@Repository
public interface JuzgadosMigracionRepository extends JpaRepository<JuzgadosMigracion, Integer> {

    @NonNull
    @EntityGraph(attributePaths = { "materiaObj", "materiaRealObj" })
    Optional<JuzgadosMigracion> findByCodigo(String codigo);

    @SuppressWarnings("null")
    @Override
    @NonNull
    @EntityGraph(attributePaths={ "materiaObj","materiaRealObj" })
    List<JuzgadosMigracion> findAll();

}