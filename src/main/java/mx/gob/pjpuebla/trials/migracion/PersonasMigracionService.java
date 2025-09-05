package mx.gob.pjpuebla.trials.migracion;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.acl.mapper.PartesMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.PersonasMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.NotificacionMapper;
import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
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
public class PersonasMigracionService {

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    private final PersonasMapper personasMapper;
    private final PartesMapper partesMapper;
    private final NotificacionMapper notificacionMapper;

    @Transactional
    public List<PersonaDocumento> createFromLegacy(List<ActoresMigracion> personas,
                                                   TipoJuicio tipoJuicio,
                                                   Carpeta carpeta) {
        if (personas == null || personas.isEmpty()) return List.of();

        List<PersonaDocumento> toSave = new ArrayList<>();
        for (var p : personas) {
            TipoPartes tipoParte = findOrCreateTipoPartes(tipoJuicio,
                    partesMapper.mapTipoPartesMigracion(p.getTipo()));

            TipoNotificacion tipoNotif = notificacionMapper.mapTipoNotificacion(p.getTipoNotificacion());

            var pd = new PersonaDocumento()
                .setNombre(p.getNombre())
                .setTipoPersona(personasMapper.mapTipoPersona(p.getTipoPersona()))
                .setRol(Rol.PRINCIPAL)
                .setCarpeta(carpeta)
                .setTipoPartes(tipoParte)
                .setIne(null).setCurp(null).setCelular(null).setCorreoElectronico(null).setDomicilio(null)
                .setTipoNotificacion(tipoNotif)
                .setCorreoNotificacion(null);

            toSave.add(pd);
        }
        return personaDocumentoRepository.saveAll(toSave);
    }

    private TipoPartes findOrCreateTipoPartes(TipoJuicio tipoJuicio, String nombre) {
        return tipoPartesRepository.findByNombreAndTipoJuicioId(nombre, tipoJuicio.getId())
            .orElseGet(() -> tipoPartesRepository.save(
                new TipoPartes().setEstado(Estado.INACTIVE).setNombre(nombre).setTipoJuicio(tipoJuicio)
            ));
    }
}
