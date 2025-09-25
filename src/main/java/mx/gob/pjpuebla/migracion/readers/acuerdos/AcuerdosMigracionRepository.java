package mx.gob.pjpuebla.migracion.readers.acuerdos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcuerdosMigracionRepository extends JpaRepository<AcuerdosMigracion, Integer>{
    
    List<AcuerdosMigracion> findByCuEntradasAndEstatus(String cuEntradas, String estado);

    List<AcuerdosMigracion> findByCuEntradasAndSentenciaInAndEstatus(String cuEntradas, List<String> tipo, String estado);
}
