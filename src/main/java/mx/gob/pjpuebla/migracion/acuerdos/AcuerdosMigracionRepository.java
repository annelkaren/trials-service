package mx.gob.pjpuebla.migracion.acuerdos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcuerdosMigracionRepository extends JpaRepository<AcuerdosMigracion, Integer>{
    
    public List<AcuerdosMigracion> findByCuEntradasAndSentenciaIn(String cuEntradas, List<String> sentencia);
}
