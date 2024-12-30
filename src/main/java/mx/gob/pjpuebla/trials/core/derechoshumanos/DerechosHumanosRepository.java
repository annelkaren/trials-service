package mx.gob.pjpuebla.trials.core.derechoshumanos;

import mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DerechosHumanosRepository extends JpaRepository<DerechosHumanos, Integer> {
    List<DerechosHumanos> findByTipoDerecho(TipoDerechosHumanos tipo);
}
