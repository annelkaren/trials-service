package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OficioCentralComisarioRepository extends JpaRepository<OficioCentralComisario, Integer> {

    Optional<OficioCentralComisario> findByDocumentoId(Integer oficioId);

}
