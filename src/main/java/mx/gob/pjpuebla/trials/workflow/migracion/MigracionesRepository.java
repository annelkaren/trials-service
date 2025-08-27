package mx.gob.pjpuebla.trials.workflow.migracion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MigracionesRepository extends JpaRepository<Migraciones, Integer>, JpaSpecificationExecutor<Migraciones>  {

    @EntityGraph(attributePaths = { "carpeta" })
    Page<Migraciones> findAll(Specification<Migraciones> spec, Pageable pageable);
}
