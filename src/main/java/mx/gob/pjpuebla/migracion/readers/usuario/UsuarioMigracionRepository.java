package mx.gob.pjpuebla.migracion.readers.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.litigante.responselitigante.AcuerdoSentenciaRecord;

public interface UsuarioMigracionRepository extends JpaRepository<UsuarioMigracion, Integer> {

    Optional<UsuarioMigracion> findByIdusuarioAndEstatus(Integer IdUsuario, String estatus);

    @Query("""
            SELECT
                new mx.gob.pjpuebla.trials.litigante.responselitigante.AcuerdoSentenciaRecord(
                    na.claveAcuerdo,
                    CONCAT(e.expediente, '/', e.amo),
                    na.fechaNotificacion,
                    j.descripcion,
                    null,
                    'Completado'
                )
            FROM UsuarioMigracion u
            JOIN NotificacionAcuerdoMigracion na ON na.idCorreo = u.idusuario AND na.tipoNotificacion = 'CO' AND na.status = 'A'
            JOIN EntradasMigracion e ON e.cu = na.cu AND e.status = 'A'
            JOIN EntradasUsuarioMigracion eu ON  eu.cuEntradas = e.cu AND eu.estatus = 'A'
            JOIN JuzgadosMigracion j ON j.codigo = e.juzgado
            WHERE u.correo = :correo
            """)
    List<AcuerdoSentenciaRecord> notificacionesLitigante(String correo);

}
