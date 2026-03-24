package mx.gob.pjpuebla.migracion.readers.juicios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JuiciosMigracionRepository extends JpaRepository<JuiciosMigracion, String> {
    
    @Query("""
            SELECT new mx.gob.pjpuebla.migracion.readers.juicios.JuicioResponseRecord(
                j.idJuicio,
                j.materia,
                j.descripcion
            )
            FROM JuiciosMigracion j
            WHERE j.idJuicio = :id
        """)
    Optional<JuicioResponseRecord> buscarPorId(String id);

    Optional<JuiciosMigracion> findByIdJuicio(String idJuicio);
}
