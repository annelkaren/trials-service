package mx.gob.pjpuebla.trials.workflow.personadetalle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonaDetalleRepository extends JpaRepository<PersonaDetalle, Integer>{
    Optional <PersonaDetalle> findByPersonaDocumentoId(Integer id);
}
