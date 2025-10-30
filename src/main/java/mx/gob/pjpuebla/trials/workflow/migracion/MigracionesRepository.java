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

/*************  ✨ Windsurf Command ⭐  *************/
    /**
     * Find all Migraciones that match the given specification.
     *
     * @param spec The specification to use when searching for Migraciones.
     * @param pageable The pageable to use when searching for Migraciones.
/*******  7a9463cc-7933-47ed-91bd-7f197e2b41a8  *******/
    @Override
    @EntityGraph(attributePaths = "carpeta")
    @NonNull
    Page<Migraciones> findAll(@Nullable Specification<Migraciones> spec, @NonNull Pageable pageable);

    Optional<Migraciones> findByCarpetaId(Integer carpetaId);
}
