package mx.gob.pjpuebla.trials.workflow.migracion;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface MigracionesRepository extends JpaRepository<Migraciones, Integer>, JpaSpecificationExecutor<Migraciones>  {

 
    @Override
    @EntityGraph(attributePaths = "carpeta")
    @NonNull
    Page<Migraciones> findAll(@Nullable Specification<Migraciones> spec, @NonNull Pageable pageable);

    Optional<Migraciones> findByCarpetaId(Integer carpetaId);
}
