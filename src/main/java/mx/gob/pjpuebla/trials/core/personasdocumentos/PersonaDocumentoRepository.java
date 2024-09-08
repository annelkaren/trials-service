package mx.gob.pjpuebla.trials.core.personasdocumentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaDocumentoRepository extends JpaRepository<PersonaDocumento, Integer> {
}
