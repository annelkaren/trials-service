package mx.gob.pjpuebla.migracion.juzgados;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;

@Repository
public interface JuzgadosMigracionRepository extends JpaRepository<JuzgadosMigracion, Integer> {

    /**
     * Spring Data JPA crea automáticamente la consulta "SELECT * FROM ... WHERE codigo = ?"
     * Usamos Optional para manejar de forma segura el caso en que no se encuentre el juzgado.
     */
    Optional<Juzgado> findByCodigo(String codigo);
}