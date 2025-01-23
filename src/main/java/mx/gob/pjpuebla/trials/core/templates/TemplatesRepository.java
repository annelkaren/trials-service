package mx.gob.pjpuebla.trials.core.templates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplatesRepository extends JpaRepository<Templates, Integer> {
    List<Templates> findByJuzgadoId(Integer juzgadoId);
}
