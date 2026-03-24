package mx.gob.pjpuebla.trials.migracion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracionSaveRecord;
import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracion;
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
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final PaisService paisService;

    @Transactional
    public List<PersonaDocumento> crearPersonaLegacy(List<ActoresMigracionSaveRecord> personas,
            TipoJuicio tipoJuicio,
            Carpeta carpeta) {
        if (personas == null || personas.isEmpty())
            return List.of();

        List<PersonaDocumento> toSave = new ArrayList<>();
        for (var p : personas) {
            TipoPartes tipoParte = findOrCreateTipoPartes(tipoJuicio,  p.tipoParte());

            TipoNotificacion tipoNotif = p.tipoNotificacion();
            String correoNotificacion = null;
            Domicilio domicilioNotificacion = null;

            if (tipoNotif.equals(TipoNotificacion.CORREO_ELECTRONICO)) {
                correoNotificacion = p.correoElectronico();
            }

            if (tipoNotif.equals(TipoNotificacion.DOMICILIO) || tipoNotif.equals(TipoNotificacion.EMPLAZAMIENTO)) {
                domicilioNotificacion = createDomicilioNotificacion(p.domicilio());
            }

            var pd = new PersonaDocumento()
                    .setNombre(p.nombre())
                    .setTipoPersona(p.tipoPersona())
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
        return tipoPartesRepository
                .findByNombreAndTipoJuicioId(nombre, tipoJuicio.getId())
                .orElseGet(() -> tipoPartesRepository.save(
                        new TipoPartes()
                                .setEstado(Estado.INACTIVE)
                                .setNombre(nombre)
                                .setTipoJuicio(tipoJuicio)));
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

    private String transformarTextoInvalido(String texto) {
        return texto.length() < 3 ? texto + "N/E" : texto;
    }
}
