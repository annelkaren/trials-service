package mx.gob.pjpuebla.trials.migracion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.acl.mapper.PartesMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PersonasMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.NotificacionMapper;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracion;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.entradasUsuarios.EntradasUsuarioMigracionReader;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracion;
import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracionReader;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.paises.Pais;
import mx.gob.pjpuebla.trials.core.paises.PaisService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonasMigracionService {

    private final DomicilioService domicilioService;
    private final DomicilioMigracionReader domicilioMigracionReader;

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final PaisService paisService;
    private final PersonasMapper personasMapper;
    private final PartesMapper partesMapper;
    private final NotificacionMapper notificacionMapper;

    private final EntradasUsuarioMigracionReader entradasUsuarioMigracionReader;
    private final UsuarioMigracionReader usuarioMigracionReader;

    @Transactional
    public List<PersonaDocumento> createFromLegacy(List<ActoresMigracion> personas,
            TipoJuicio tipoJuicio,
            Carpeta carpeta) {
        if (personas == null || personas.isEmpty())
            return List.of();

        List<PersonaDocumento> toSave = new ArrayList<>();
        for (var p : personas) {
            TipoPartes tipoParte = findOrCreateTipoPartes(tipoJuicio,
                    partesMapper.mapTipoPartesMigracion(p.getTipo()));

            TipoNotificacion tipoNotif = notificacionMapper.mapTipoNotificacion(p.getTipoNotificacion());
            String correoNotificacion = null;
            Domicilio domicilioNotificacion = null;

            if (tipoNotif.equals(TipoNotificacion.CORREO_ELECTRONICO)) {
                correoNotificacion = entradasUsuarioMigracionReader
                        .findByClaveActorAndEstatus(p.getClaveAct())
                        .map(entradaUsuario -> {
                            int idUsuario = entradaUsuario.getIdusuario();
                            UsuarioMigracion usuario = usuarioMigracionReader
                                    .findUsuarioMigracionByIdUsuarioAnEstado(idUsuario);
                            return (usuario != null) ? usuario.getCorreo() : "";
                        })
                        .orElse("");
            }

            if (tipoNotif.equals(TipoNotificacion.DOMICILIO) || tipoNotif.equals(TipoNotificacion.EMPLAZAMIENTO)) {
                DomicilioMigracion domicilioMigracion = domicilioMigracionReader
                        .findByCuActorAndEstado(p.getClaveAct());
                domicilioNotificacion = createDomicilioNotificacion(domicilioMigracion);
               
            }

            String nombrePersona = p.getNombre().trim().length() < 3 ? p.getNombre().trim() + " N/E" : p.getNombre().trim();

            var pd = new PersonaDocumento()
                    .setNombre(nombrePersona)
                    .setTipoPersona(personasMapper.mapTipoPersona(p.getTipoPersona()))
                    .setRol(Rol.PRINCIPAL)
                    .setCarpeta(carpeta)
                    .setTipoPartes(tipoParte)
                    .setIne(null)
                    .setCurp(null)
                    .setCelular(null)
                    .setCorreoElectronico(null)
                    .setDomicilio(null)
                    .setFnDomicilio(domicilioNotificacion)
                    .setTipoNotificacion(tipoNotif)
                    .setCorreoNotificacion(correoNotificacion);

            toSave.add(pd);
        }
        return personaDocumentoRepository.saveAll(toSave);
    }

    private TipoPartes findOrCreateTipoPartes(TipoJuicio tipoJuicio, String nombre) {
       
        Optional<TipoPartes> tipoPartes = tipoPartesRepository.findByNombreAndTipoJuicioId(nombre, tipoJuicio.getId());
        if (tipoPartes.isPresent()) {
           
            return tipoPartes.get();
        }else{
          
            return tipoPartesRepository.save(
                        new TipoPartes().setEstado(Estado.INACTIVE).setNombre(nombre).setTipoJuicio(tipoJuicio));
        }
     
    }

    private Domicilio createDomicilioNotificacion(DomicilioMigracion domicilio) {
        // obtenemos registro del pais de México y lo asociamos al nuevo registro.

        Pais pais = paisService.findByNombreComun("México");

        return domicilioService.save(new Domicilio()
                .setCalle(transformarTextoInvalido(domicilio.getCalle()))
                .setInterior(transformarTextoInvalido(domicilio.getNumin()))
                .setExterior(transformarTextoInvalido(domicilio.getNumex()))
                .setColonia(transformarTextoInvalido(domicilio.getColonia()))
                // .setLocalidad() no se puede obtener la localidad desde legacy
                .setCodigoPostal(transformarTextoInvalido(domicilio.getCp().toString()))
                .setMunicipio(transformarTextoInvalido(domicilio.getMunicipio()))
                .setEstadoRepublica(transformarTextoInvalido(domicilio.getEstado()))
                // .setReferencia() no se puede obtener referencias desde legacy
                .setPaisResidencia(pais)
                .setLatitud(transformarTextoInvalido(domicilio.getLatitud().toString()))
                .setLongitud(transformarTextoInvalido(domicilio.getLongitud().toString()))
                .setCiudad(transformarTextoInvalido(domicilio.getCiudad())));
    }

 private String transformarTextoInvalido(String texto){
        return texto.length() < 3 ? texto + "N/E" : texto;
    }
}
