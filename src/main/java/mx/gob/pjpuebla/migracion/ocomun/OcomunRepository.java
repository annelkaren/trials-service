package mx.gob.pjpuebla.migracion.ocomun;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcomunRepository extends JpaRepository<Ocomun, Integer> {

    Optional<Ocomun> findTopByCuAndEstatusOrderByIdDesc(String cu, String estatus);

}
