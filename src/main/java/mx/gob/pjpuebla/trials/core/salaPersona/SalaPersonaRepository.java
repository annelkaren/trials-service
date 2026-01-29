package mx.gob.pjpuebla.trials.core.salaPersona;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.core.salas.SecretariosSalasRecord;

public interface SalaPersonaRepository extends JpaRepository<SalaPersona, Integer> {
    
    @Query("""
            SELECT 
                new mx.gob.pjpuebla.trials.core.salas.SecretariosSalasRecord(
                    sp.id,
                    sala.id,
                    persona.id,
                    COALESCE(persona.nombre, '') || ' ' || COALESCE(persona.apellidoPaterno, '') || ' ' ||COALESCE(persona.apellidoMaterno, ''),
                    sp.rol
                   )
            FROM SalaPersona sp
            JOIN sp.persona persona
            JOIN sp.sala sala 
            WHERE sp.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND sp.sala.id = :salaId
            """)
    List<SecretariosSalasRecord> getSecretariosFromSala(Integer salaId);
    
}
