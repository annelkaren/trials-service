package mx.gob.pjpuebla.migracion.readers.acuerdos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.litigante.DocumentoResponseRecord;

@Repository
public interface AcuerdosMigracionRepository extends JpaRepository<AcuerdosMigracion, Integer> {

    List<AcuerdosMigracion> findByCuEntradasAndEstatus(String cuEntradas, String estado);

    List<AcuerdosMigracion> findByCuEntradasAndSentenciaInAndEstatus(String cuEntradas, List<String> tipo,
            String estado);

    Optional<AcuerdosMigracion> findByClave(Integer clave);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.litigante.DocumentoResponseRecord(
                acuerdo.clave,
                acuerdo.fecha,
                acuerdo.resumen,
                acuerdo.ruta
            )
            FROM EntradasMigracion entrada
            JOIN EntradasUsuarioMigracion eu ON eu.cuEntradas = entrada.cu AND eu.estatus = 'A'AND entrada.status = 'A'
            JOIN UsuarioMigracion u ON u.idusuario = eu.idusuario AND u.estatus = 'A'
            JOIN AcuerdosMigracion acuerdo ON acuerdo.cuEntradas = entrada.cu AND acuerdo.estatus = 'A'
            WHERE u.correo = :correo and acuerdo.cuEntradas = :cu
            """)
    Page<DocumentoResponseRecord> findDetailsExpedienteLitigante(String correo, String cu, Pageable pageable);

}
