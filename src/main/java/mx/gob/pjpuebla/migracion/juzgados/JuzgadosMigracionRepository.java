package mx.gob.pjpuebla.migracion.juzgados;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JuzgadosMigracionRepository extends JpaRepository<JuzgadosMigracion, Integer> {

    /**
     * Spring Data JPA crea automáticamente la consulta "SELECT * FROM ... WHERE codigo = ?"
     * Usamos Optional para manejar de forma segura el caso en que no se encuentre el juzgado.
     */
    Optional<JuzgadosMigracion> findByCodigo(String codigo);

}