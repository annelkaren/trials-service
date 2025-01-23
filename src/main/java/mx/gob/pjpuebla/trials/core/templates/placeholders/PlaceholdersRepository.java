package mx.gob.pjpuebla.trials.core.templates.placeholders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceholdersRepository  extends JpaRepository<Placeholders, Integer> {
}
