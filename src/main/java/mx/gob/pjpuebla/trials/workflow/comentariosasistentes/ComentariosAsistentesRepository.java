package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentariosAsistentesRepository extends JpaRepository<ComentariosAsistentes, Integer> {

    Page<ComentariosAsistentes> findByPersonaDocumentoId(Integer personaDocumentoId, Pageable pageable);
}
