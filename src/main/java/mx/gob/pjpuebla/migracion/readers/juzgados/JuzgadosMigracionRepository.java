package mx.gob.pjpuebla.migracion.readers.juzgados;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JuzgadosMigracionRepository extends JpaRepository<JuzgadosMigracion, Integer> {

    /**
     * Spring Data JPA crea automáticamente la consulta "SELECT * FROM ... WHERE codigo = ?"
     * Usamos Optional para manejar de forma segura el caso en que no se encuentre el juzgado.
     */
    @EntityGraph(attributePaths={ "materiaObj","materiaRealObj" })
    Optional<JuzgadosMigracion> findByCodigo(String codigo);

    @EntityGraph(attributePaths={ "materiaObj","materiaRealObj" })
    List<JuzgadosMigracion> findAll();

}