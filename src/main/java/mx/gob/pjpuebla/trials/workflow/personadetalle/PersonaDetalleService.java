package mx.gob.pjpuebla.trials.workflow.personadetalle;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionRepository;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigena;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigenaRepository;
import mx.gob.pjpuebla.trials.core.nacionalidades.Nacionalidad;
import mx.gob.pjpuebla.trials.core.nacionalidades.NacionalidadRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoCondicionMigratoria;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoDiscapacidades;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoEstadoCivil;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoFrecuenciaIngreso;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoGrupoVulnerable;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoIngresoMensualNeto;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoProfesionOficio;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoTipoDefensor;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoTiposDomicilio;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonaDetalleService {

    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final CarpetaRepository carpetaRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final LenguaIndigenaRepository lenguaIndigenaRepository;
    private final NacionalidadRepository nacionalidadRepository;
    private final DocumentoIdentificacionRepository documentoIdentificacionRepository;
    private final EscolaridadRepository escolaridadRepository;
    private final DomicilioRepository domicilioRepository;
    private final PersonaDetalleRepository personaDetalleRepository;

    public static <E extends Enum<E>> Integer obtenerOrdinalDesdeString(String texto, Class<E> enumClass) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> {
                    try {
                        String etiqueta = (String) enumClass.getMethod("getEtiqueta").invoke(enumValue);
                        if (etiqueta != null && etiqueta.equalsIgnoreCase(texto)) {
                            return true;
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                    }
                   
                    return enumValue.name().equalsIgnoreCase(texto);
                })
                .findFirst()
                .map(Enum::ordinal)
                .orElseThrow(() -> new IllegalArgumentException("Valor no válido: " + texto));
    }
    
    
    
    public PersonaDetalleRecord createPersonaDetalle(PersonaDTO personaDTO){
       
        Integer idCarpeta = personaDTO.getDatosGenerales().getIdCarpeta();
        Carpeta carpeta = carpetaRepository.findById(idCarpeta).orElseThrow(() -> new EntityNotFoundException("Carpeta no encontrada"));
        
        TipoJuicio tipoJucio = carpeta.getTipoJuicio();
        Integer tipoJuicioId = tipoJucio.getId();

        String tipoParteNombre = personaDTO.getDatosGenerales().getTipo().get(0);
        TipoPartes tipoPartes = tipoPartesRepository.findByNombreAndTipoJuicioId(tipoParteNombre, tipoJuicioId)
            .orElseThrow(() -> new EntityNotFoundException("Tipo de partes no encontrado")); // Handle Optional

        PersonaDocumento personaDocumento = new PersonaDocumento();

        if(!personaDTO.getDatosGenerales().getNombre().equals("")){
            personaDocumento.setNombre(personaDTO.getDatosGenerales().getNombre());
        } else if (!personaDTO.getDatosGenerales().getRazonSocial().equals("")){
            personaDocumento.setNombre(personaDTO.getDatosGenerales().getRazonSocial());
        }
        personaDocumento.setApellidoPaterno(personaDTO.getDatosGenerales().getApellidoPaterno());
        personaDocumento.setApellidoMaterno(personaDTO.getDatosGenerales().getApellidoMaterno());
        personaDocumento.setPseudonimo(personaDTO.getDatosGenerales().getPseudonimo());
        personaDocumento.setTipoPersona(personaDTO.getDatosGenerales().getTipoPersona());
        personaDocumento.setCarpeta(carpeta);
        personaDocumento.setTipoPartes(tipoPartes);
        personaDocumento.setCurp(personaDTO.getDatosGenerales().getCurp());
        personaDocumento.setCelular(personaDTO.getDatosContacto().getTelefono());  
        personaDocumento.setCorreoElectronico(personaDTO.getDatosContacto().getCorreoElectronico());
        
        personaDocumentoRepository.save(personaDocumento);

        Domicilio domicilio = new Domicilio();
        domicilio.setCalle(personaDTO.getDatosContacto().getCalle());
        domicilio.setInterior(personaDTO.getDatosContacto().getNumeroInterior());
        domicilio.setExterior(personaDTO.getDatosContacto().getNumeroExterior());
        domicilio.setColonia(personaDTO.getDatosContacto().getColonia());
        domicilio.setCodigoPostal(personaDTO.getDatosContacto().getCodigoPostal());
        domicilio.setMunicipio(personaDTO.getDatosContacto().getMunicipio());
        domicilio.setEstadoRepublica(personaDTO.getDatosContacto().getEstado());

        domicilioRepository.save(domicilio);

        PersonaDetalle personaDetalle = new PersonaDetalle();

        if (personaDTO.getDatosEstadistica().getLenguaIndigena() != null) {
            LenguaIndigena lenguaIndigena = lenguaIndigenaRepository.findById(personaDTO.getDatosEstadistica().getLenguaIndigena())
                .orElseThrow(() -> new EntityNotFoundException("Lengua indígena no encontrada"));
            personaDetalle.setLenguaIndigena(lenguaIndigena);
        } else {
            personaDetalle.setLenguaIndigena(null);
        }
        
        if (personaDTO.getDatosGenerales().getNacionalidad() != null) {
            Nacionalidad nacionalidad = nacionalidadRepository.findById(personaDTO.getDatosGenerales().getNacionalidad())
                .orElseThrow(() -> new EntityNotFoundException("Nacionalidad no encontrada"));
            personaDetalle.setNacionalidad(nacionalidad);
        } else {
            personaDetalle.setNacionalidad(null);
        }
        
        if (personaDTO.getDatosEstadistica().getEscolaridad() != null) {
            Escolaridad escolaridad = escolaridadRepository.findById(personaDTO.getDatosEstadistica().getEscolaridad())
                .orElseThrow(() -> new EntityNotFoundException("Escolaridad no encontrada"));
            personaDetalle.setEscolaridad(escolaridad);
        } else {
            personaDetalle.setEscolaridad(null);
        }
        
        if (personaDTO.getDatosEstadistica().getDocumento() != null) {
            DocumentoIdentificacion documentoIdentificacion = documentoIdentificacionRepository.findById(personaDTO.getDatosEstadistica().getDocumento())
                .orElseThrow(() -> new EntityNotFoundException("Documento Identificación no encontrado"));
            personaDetalle.setDocumentoIdentificacion(documentoIdentificacion);
        } else {
            personaDetalle.setDocumentoIdentificacion(null);
        }
       
        personaDetalle.setPersonaDocumento(personaDocumento);
        personaDetalle.setDomicilio(domicilio);
        personaDetalle.setSexo(personaDTO.getDatosGenerales().getSexo());
        LocalDate fechaNacimientoStr = personaDTO.getDatosGenerales().getFechaNacimiento();
        if (fechaNacimientoStr != null) {
            personaDetalle.setFechaNacimiento(java.sql.Date.valueOf(personaDTO.getDatosGenerales().getFechaNacimiento()));
        } else {
            personaDetalle.setFechaNacimiento(null); 
        } 
        personaDetalle.setPaisNacimiento(personaDTO.getDatosEstadistica().getPaisNacimiento());
        personaDetalle.setMunicipioNacimiento(personaDTO.getDatosEstadistica().getMunicipioNacimiento());
        personaDetalle.setEntidadNacimiento(personaDTO.getDatosEstadistica().getEntidadNacimiento());
        personaDetalle.setEdad(personaDTO.getDatosGenerales().getEdad());

        personaDetalle.setLenguaExtranjera(personaDTO.getDatosEstadistica().getLenguaExtranjeraDetalle());
        personaDetalle.setSabeLeerEscribir(personaDTO.getDatosEstadistica().getSabeLeer()); 
        personaDetalle.setLugarTrabajo(personaDTO.getDatosEstadistica().getLugarTrabajo());
        personaDetalle.setCantidadDependientesEconomicos(personaDTO.getDatosEstadistica().getCantidadDependientes());
        personaDetalle.setDetalleDependientes(personaDTO.getDatosEstadistica().getDetallesDependientes());
        personaDetalle.setBienes(personaDTO.getDatosEstadistica().getCantidadBienes());
        personaDetalle.setReligion(personaDTO.getDatosEstadistica().getReligion());
        personaDetalle.setSeniasParticulares(personaDTO.getDatosEstadistica().getSenias());
        personaDetalle.setGrupoEtnico(personaDTO.getDatosEstadistica().getGrupoEtnico());
        personaDetalle.setDatosPrivados(personaDTO.getDatosEstadistica().getDatosPrivados());
        personaDetalle.setCedula(personaDTO.getDatosGenerales().getCedula());
        personaDetalle.setAdscripcion(personaDTO.getDatosGenerales().getAdscripcion());
        
        String tipoDomicilioTexto = personaDTO.getDatosContacto().getTipoDomicilio();
        personaDetalle.setTipoDomicilio(obtenerOrdinalDesdeString(tipoDomicilioTexto, CatalogoTiposDomicilio.class));
        
        String discapacidadTexto = personaDTO.getDatosEstadistica().getDiscapacidad();
        personaDetalle.setDiscapacidad(obtenerOrdinalDesdeString(discapacidadTexto, CatalogoDiscapacidades.class));
        
        String estadoCivilTexto = personaDTO.getDatosGenerales().getEstadoCivil().toString(); 
        personaDetalle.setEstadoCivil(obtenerOrdinalDesdeString(estadoCivilTexto, CatalogoEstadoCivil.class));

        String condicionMigratoriaTexto = personaDTO.getDatosEstadistica().getCondicionMigratoria();
        personaDetalle.setCondicionMigratoria(obtenerOrdinalDesdeString(condicionMigratoriaTexto, CatalogoCondicionMigratoria.class));
        
        String grupoVulnerableTexto = personaDTO.getDatosEstadistica().getGrupoVulnerable();
        personaDetalle.setGrupoVulnerable(obtenerOrdinalDesdeString(grupoVulnerableTexto, CatalogoGrupoVulnerable.class));
        
        String profesionTexto = personaDTO.getDatosEstadistica().getProfesion();
        personaDetalle.setProfesionOficio(obtenerOrdinalDesdeString(profesionTexto, CatalogoProfesionOficio.class));
       
        String ingresoNetoTexto = personaDTO.getDatosEstadistica().getIngresosMensuales();
        personaDetalle.setIngresoMensualNeto(obtenerOrdinalDesdeString(ingresoNetoTexto, CatalogoIngresoMensualNeto.class));
       
        String frecuenciaIngresoTexto = personaDTO.getDatosEstadistica().getFrecuenciaIngreso();
        personaDetalle.setFrecuenciaIngreso(obtenerOrdinalDesdeString(frecuenciaIngresoTexto, CatalogoFrecuenciaIngreso.class));
        
        String tipoDefensorTexto = personaDTO.getDatosGenerales().getTipoDefensor();
        personaDetalle.setTipoDefensor(obtenerOrdinalDesdeString(tipoDefensorTexto, CatalogoTipoDefensor.class));
        
        Boolean hablaEspanol = personaDTO.getDatosEstadistica().getHablaEspanol();
        personaDetalle.setHablaEspanol(hablaEspanol);
       
        personaDetalleRepository.save(personaDetalle);

        return new PersonaDetalleRecord(personaDetalle.getId(), personaDocumento.getId(), domicilio.getId());
    }
        
}
