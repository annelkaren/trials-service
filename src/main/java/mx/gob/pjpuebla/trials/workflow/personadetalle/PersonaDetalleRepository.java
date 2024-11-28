package mx.gob.pjpuebla.trials.workflow.personadetalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaDetalleRepository extends JpaRepository<PersonaDetalle, Integer>{
    Optional<PersonaDetalle> findByPersonaDocumentoId(Integer pnId);
}
