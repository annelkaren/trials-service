package mx.gob.pjpuebla.trials.core.anexos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Integer> {
}
