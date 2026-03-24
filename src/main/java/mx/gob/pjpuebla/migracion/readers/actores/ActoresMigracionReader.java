package mx.gob.pjpuebla.migracion.readers.actores;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.acl.mapper.NotificacionMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PartesMapper;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracion;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.entradasUsuarios.EntradasUsuarioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracion;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracionReader;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActoresMigracionReader {

    private final ActoresMigracionRepository actoresMigracionRepository;
    private final PartesMapper partesMapper;
    private final NotificacionMapper notificacionMapper;
    private final EntradasUsuarioMigracionReader entradasUsuariosReader;
    private final DomicilioMigracionReader domicilioMigracionReader;
    private final UsuarioMigracionReader usuarioMigracionReader;

    /**
     * Busca todos los actores asociados a una clave específica.
     *
     * @param clave Clave de búsqueda
     * @return Lista de actores encontrados
     */
    public List<ActoresMigracion> buscarPorClave(String clave) {
        return actoresMigracionRepository.findByClave(clave);
    }

    public List<ActoresMigracionSaveRecord> getActoresExpediente(List<ActoresMigracion> actores) {
        if (actores == null || actores.isEmpty()) {
            return List.of();
        }

        return actores.stream()
                .map(this::mapActorMigracion)
                .toList();
    }

    private ActoresMigracionSaveRecord mapActorMigracion(ActoresMigracion a) {
        String tipoPartes = partesMapper.mapTipoPartesMigracion(a.getTipo());
        TipoNotificacion tipoNotificacion = notificacionMapper.mapTipoNotificacion(a.getTipoNotificacion());
        String correoNotificacion = resolveCorreoNotificacion(a.getClaveAct());
        DomicilioMigracion domicilioMigracion = domicilioMigracionReader.findByCuActorAndEstado(a.getClaveAct());
        String nombrePersona = normalizeNombre(a.getNombre());

        return new ActoresMigracionSaveRecord(
                tipoPartes,
                tipoNotificacion,
                correoNotificacion,
                domicilioMigracion,
                nombrePersona,
                a.getTipoPersona());
    }

    private String resolveCorreoNotificacion(String claveActor) {
        return entradasUsuariosReader.findByClaveActorAndEstatus(claveActor)
                .map(entradaUsuario -> {
                    int idUsuario = entradaUsuario.getIdusuario();
                    UsuarioMigracion usuario = usuarioMigracionReader
                            .findUsuarioMigracionByIdUsuarioAnEstado(idUsuario);
                    return (usuario != null) ? usuario.getCorreo() : "";
                })
                .orElse("");
    }

    private String normalizeNombre(String nombre) {
        if (nombre == null) {
            return "N/E";
        }
        String trimmed = nombre.trim();
        return trimmed.length() < 3 ? trimmed + " N/E" : trimmed;
    }

}