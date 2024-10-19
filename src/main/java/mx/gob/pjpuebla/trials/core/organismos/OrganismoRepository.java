package mx.gob.pjpuebla.trials.core.organismos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganismoRepository extends JpaRepository<Organismo, Integer> {

}