package mx.gob.pjpuebla.migracion.readers.usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioMigracionRepository extends JpaRepository<UsuarioMigracion, Integer> {
    
    Optional<UsuarioMigracion> findByIdUsuarioAndEstatus(Integer IdUsuario, String estatus);

}
