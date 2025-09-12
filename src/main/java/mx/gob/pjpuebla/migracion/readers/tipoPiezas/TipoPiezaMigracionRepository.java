package mx.gob.pjpuebla.migracion.readers.tipoPiezas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoPiezaMigracionRepository extends JpaRepository<TipoPiezaMigracion, Integer> {
     
    Optional<TipoPiezaMigracion> findByClave(String clave);
}
