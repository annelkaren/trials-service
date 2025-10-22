package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionSinExpedienteRepository extends JpaRepository<PromocionSinExpediente, Integer> {

}
