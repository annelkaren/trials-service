package mx.gob.pjpuebla.trials.workflow.personadetalle;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import mx.gob.pjpuebla.trials.core.paises.Pais;
import mx.gob.pjpuebla.trials.core.paises.PaisRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTO;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTOGet;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

@Slf4j
@Transactional
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
    private final PaisRepository paisRepository;

    public PersonaDetalleRecord createPersonaDetalle(PersonaDTO personaDTO){
       
        Integer idCarpeta = personaDTO.getDatosGenerales().getIdCarpeta();
        Carpeta carpeta = carpetaRepository.findById(idCarpeta).orElseThrow(() -> new EntityNotFoundException("Carpeta no encontrada"));
        
        Integer tipoParteId = personaDTO.getDatosGenerales().getTipo();
        TipoPartes tipoParte = tipoPartesRepository.findById(tipoParteId)
            .orElseThrow(() -> new EntityNotFoundException("Tipo de partes no encontrado")); 

        PersonaDocumento personaDocumento = new PersonaDocumento();

        if(!personaDTO.getDatosGenerales().getNombres().isEmpty()){
            personaDocumento.setNombre(personaDTO.getDatosGenerales().getNombres());
        } else if (!personaDTO.getDatosGenerales().getRazonSocial().isEmpty()){
            personaDocumento.setNombre(personaDTO.getDatosGenerales().getRazonSocial());
        }
        
        personaDocumento.setApellidoPaterno(personaDTO.getDatosGenerales().getApellidoPaterno());
        personaDocumento.setApellidoMaterno(personaDTO.getDatosGenerales().getApellidoMaterno());
        personaDocumento.setPseudonimo(personaDTO.getDatosGenerales().getPseudonimo());
        personaDocumento.setTipoPersona(personaDTO.getDatosGenerales().getTipoPersona());
        personaDocumento.setCarpeta(carpeta);
        personaDocumento.setTipoPartes(tipoParte);
        personaDocumento.setCurp(personaDTO.getDatosGenerales().getCurp());
        personaDocumento.setCelular(personaDTO.getDatosContacto().getTelefono());  
        personaDocumento.setCorreoElectronico(personaDTO.getDatosContacto().getCorreoElectronico());

        
        Domicilio domicilio = new Domicilio();
        
        domicilio.setCalle(personaDTO.getDatosContacto().getCalle());
        domicilio.setInterior(personaDTO.getDatosContacto().getNumeroInterior());
        domicilio.setExterior(personaDTO.getDatosContacto().getNumeroExterior());
        domicilio.setColonia(personaDTO.getDatosContacto().getColonia());
        domicilio.setCodigoPostal(personaDTO.getDatosContacto().getCodigoPostal());
        domicilio.setMunicipio(personaDTO.getDatosContacto().getMunicipio());
        domicilio.setEstadoRepublica(personaDTO.getDatosContacto().getEstado());

        if( personaDTO.getDatosContacto().getPais() != null){
            Integer idPaisResidencia = personaDTO.getDatosContacto().getPais();
            Pais paisResidencia = paisRepository.findById(idPaisResidencia).orElseThrow(() -> new EntityNotFoundException("Pais de residencia no encontrado"));  
            domicilio.setPaisResidencia(paisResidencia);
        } 


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
        
        if (personaDTO.getDatosEstadistica().getDocumentoIdentificacion() != null) {
            DocumentoIdentificacion documentoIdentificacion = documentoIdentificacionRepository.findById(personaDTO.getDatosEstadistica().getDocumentoIdentificacion())
                .orElseThrow(() -> new EntityNotFoundException("Documento Identificación no encontrado"));
            personaDetalle.setDocumentoIdentificacion(documentoIdentificacion);
        } else {
            personaDetalle.setDocumentoIdentificacion(null);
        }
       
        if (personaDTO.getDatosGenerales().getEnRepresentacion() != null) {
            PersonaDocumento enRepresentacion = personaDocumentoRepository.findById(personaDTO.getDatosGenerales().getEnRepresentacion())
                .orElseThrow(() -> new EntityNotFoundException("Persona representada no encontrada"));
            personaDetalle.setEnRepresentacionDe(enRepresentacion);
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
       
        personaDetalle.setMunicipioNacimiento(personaDTO.getDatosEstadistica().getMunicipioNacimiento());
        personaDetalle.setEntidadNacimiento(personaDTO.getDatosEstadistica().getEntidadNacimiento());
        personaDetalle.setEdad(personaDTO.getDatosGenerales().getEdad());

        if(personaDTO.getDatosEstadistica().getPaisNacimiento() != null){
            Integer idPaisNacimiento = personaDTO.getDatosEstadistica().getPaisNacimiento();
            Pais paisNacimiento = paisRepository.findById(idPaisNacimiento).orElseThrow(() -> new EntityNotFoundException("Pais de nacimiento no encontrado"));
            personaDetalle.setPaisNacimiento(paisNacimiento);
        }

        personaDetalle.setLenguaExtranjera(personaDTO.getDatosEstadistica().getLenguaExtranjera());
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

        personaDetalle.setTipoDomicilio(personaDTO.getDatosContacto().getTipoDomicilio());
        
        personaDetalle.setDiscapacidad(personaDTO.getDatosEstadistica().getDiscapacidad());
        
        personaDetalle.setEstadoCivil(personaDTO.getDatosGenerales().getEstadoCivil());

        personaDetalle.setCondicionMigratoria(personaDTO.getDatosEstadistica().getCondicionMigratoria());
        
        personaDetalle.setGrupoVulnerable(personaDTO.getDatosEstadistica().getGrupoVulnerable());
        
        personaDetalle.setProfesionOficio(personaDTO.getDatosEstadistica().getProfesion());
    
        personaDetalle.setIngresoMensualNeto(personaDTO.getDatosEstadistica().getIngresosMensuales());
       
        personaDetalle.setFrecuenciaIngreso(personaDTO.getDatosEstadistica().getFrecuenciaIngreso());

        personaDetalle.setTipoDefensor(personaDTO.getDatosGenerales().getTipoDefensor());
        
        personaDetalle.setRfc(personaDTO.getDatosGenerales().getRfc());
        
        // BOOLEAN  
        personaDetalle.setHablaEspanol(personaDTO.getDatosEstadistica().getHablaEspanol());

        personaDetalle.setRecibePercepciones(personaDTO.getDatosEstadistica().getRecibePercepciones());

        personaDetalle.setHablaLenguaIndigena(personaDTO.getDatosEstadistica().getHablaLenguaIndigena());
       
        personaDetalle.setPerteneceGrupoVulnerable(personaDTO.getDatosEstadistica().getPerteneceGrupoVulnerable());
       
        personaDetalle.setTieneBienes(personaDTO.getDatosEstadistica().getTieneBienes());

        personaDetalle.setTieneSeniasParticulares(personaDTO.getDatosEstadistica().getTieneSeniasParticulares());

        personaDetalle.setPerteneceGrupoEtnico(personaDTO.getDatosEstadistica().getPerteneceGrupoEtnico());
       
        personaDetalle.setTieneReligion(personaDTO.getDatosEstadistica().getTieneReligion());
       
        personaDetalle.setTieneDependientes(personaDTO.getDatosEstadistica().getTieneDependientes());
       
        personaDetalle.setHablaLenguaExtranjera(personaDTO.getDatosEstadistica().getHablaLenguaExtranjera()); 
       
        personaDocumentoRepository.save(personaDocumento);
        domicilioRepository.save(domicilio);
        personaDetalleRepository.save(personaDetalle);
               
        return new PersonaDetalleRecord(personaDetalle.getId(),personaDocumento.getId(), domicilio.getId());
    }

    public PersonaDTOGet getParticipante(Integer id){
        PersonaDocumento personaDocumento = personaDocumentoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("PersonaDocumento no encontrado", "id"));

        PersonaDetalle personaDetalle = personaDetalleRepository.findByPersonaDocumentoId(id)
        .orElseThrow(() -> new NotFoundException("PersonaDocumento no encontrado", ""));

        Domicilio domicilio = domicilioRepository.findById(personaDetalle.getDomicilio().getId())
        .orElseThrow(() -> new NotFoundException("PersonaDocumento no encontrado", ""));

        PersonaDTOGet personaDTOGet = new PersonaDTOGet();

        personaDTOGet.setPersonaDocumentoId(id);
        personaDTOGet.setDomicilioId(domicilio.getId());
        personaDTOGet.setPersonaDetalleId(personaDetalle.getId());

        PersonaDTOGet.DatosGenerales datosGenerales = new PersonaDTOGet.DatosGenerales();
        datosGenerales.setApellidoPaterno(personaDocumento.getApellidoPaterno());
        datosGenerales.setApellidoMaterno(personaDocumento.getApellidoMaterno());
        datosGenerales.setCurp(personaDocumento.getCurp());
        datosGenerales.setCedula(personaDetalle.getCedula());
        datosGenerales.setEstadoCivil(personaDetalle.getEstadoCivil());
        datosGenerales.setFechaNacimiento(personaDetalle.getFechaNacimiento());
        datosGenerales.setSexo(personaDetalle.getSexo());
        datosGenerales.setNacionalidad(personaDetalle.getNacionalidad());
        datosGenerales.setAdscripcion(personaDetalle.getAdscripcion());
        datosGenerales.setCurp(personaDocumento.getCurp());
        datosGenerales.setEdad(personaDetalle.getEdad());
        datosGenerales.setEnRepresentacion(personaDetalle.getEnRepresentacionDe());
        datosGenerales.setPseudonimo(personaDocumento.getPseudonimo());
        datosGenerales.setRfc(personaDetalle.getRfc());
        datosGenerales.setSexo(personaDetalle.getSexo());
        datosGenerales.setEstadoCivil(personaDetalle.getEstadoCivil());
        if(personaDocumento.getTipoPersona().equalsIgnoreCase("fisica")){
            datosGenerales.setNombre(personaDocumento.getNombre());
        } else {
            datosGenerales.setRazonSocial(personaDocumento.getNombre());
        }
               
        datosGenerales.setPseudonimo(personaDocumento.getPseudonimo());
        datosGenerales.setTipo(personaDocumento.getTipoPartes().getId());
        datosGenerales.setTipoDefensor(personaDetalle.getTipoDefensor());
        datosGenerales.setTipoPersona(personaDocumento.getTipoPersona());        
        personaDTOGet.setDatosGenerales(datosGenerales);

        personaDTOGet.setDatosGenerales(datosGenerales);

        PersonaDTOGet.DatosContacto datosContacto = new PersonaDTOGet.DatosContacto();

        datosContacto.setCalle(domicilio.getCalle());
        datosContacto.setCodigoPostal(domicilio.getCodigoPostal());
        datosContacto.setColonia(domicilio.getColonia());
        datosContacto.setCorreoElectronico(personaDocumento.getCorreoElectronico());
        datosContacto.setEstado(domicilio.getEstadoRepublica());
        datosContacto.setMunicipio(domicilio.getMunicipio());
        datosContacto.setNumeroExterior(domicilio.getExterior());
        datosContacto.setNumeroInterior(domicilio.getInterior());
        datosContacto.setTelefono(personaDocumento.getCelular());
        datosContacto.setTipoDomicilio(personaDetalle.getTipoDomicilio());
        datosContacto.setPais(domicilio.getPaisResidencia());

        personaDTOGet.setDatosContacto(datosContacto);

        PersonaDTOGet.DatosEstadistica datosEstadistica = new PersonaDTOGet.DatosEstadistica();

        datosEstadistica.setCantidadBienes(personaDetalle.getBienes());
        datosEstadistica.setCantidadDependientes(personaDetalle.getCantidadDependientesEconomicos());
        datosEstadistica.setCondicionMigratoria(personaDetalle.getCondicionMigratoria());
        datosEstadistica.setDatosPrivados(personaDetalle.getDatosPrivados());
        datosEstadistica.setDetallesDependientes(personaDetalle.getDetalleDependientes());
        datosEstadistica.setDiscapacidad(personaDetalle.getDiscapacidad());
        datosEstadistica.setDocumento(personaDetalle.getDocumentoIdentificacion());
        datosEstadistica.setEntidadNacimiento(personaDetalle.getEntidadNacimiento());
        datosEstadistica.setEscolaridad(personaDetalle.getEscolaridad());
        datosEstadistica.setFrecuenciaIngreso(personaDetalle.getFrecuenciaIngreso());
        datosEstadistica.setGrupoEtnico(personaDetalle.getGrupoEtnico());
        datosEstadistica.setGrupoVulnerable(personaDetalle.getGrupoVulnerable());
        datosEstadistica.setHablaEspanol(personaDetalle.getHablaEspanol());
        datosEstadistica.setIngresosMensuales(personaDetalle.getIngresoMensualNeto());
        datosEstadistica.setLenguaIndigena(personaDetalle.getLenguaIndigena());
        datosEstadistica.setLugarTrabajo(personaDetalle.getLugarTrabajo());
        datosEstadistica.setMunicipioNacimiento(personaDetalle.getMunicipioNacimiento());
        datosEstadistica.setPaisNacimiento(personaDetalle.getPaisNacimiento());
        datosEstadistica.setProfesion(personaDetalle.getProfesionOficio());
        datosEstadistica.setRecibePercepciones(personaDetalle.getRecibePercepciones());
        datosEstadistica.setReligion(personaDetalle.getReligion());
        datosEstadistica.setSabeLeer(personaDetalle.getSabeLeerEscribir());
        datosEstadistica.setSenias(personaDetalle.getSeniasParticulares());
        datosEstadistica.setCantidadDependientes(personaDetalle.getCantidadDependientesEconomicos());
        datosEstadistica.setHablaLenguaIndigena(personaDetalle.getHablaLenguaIndigena());
        datosEstadistica.setPerteneceGrupoVulnerable(personaDetalle.getPerteneceGrupoVulnerable());
        datosEstadistica.setTieneBienes(personaDetalle.getTieneBienes());
        datosEstadistica.setTieneSeniasParticulares(personaDetalle.getTieneSeniasParticulares());
        datosEstadistica.setPerteneceGrupoEtnico(personaDetalle.getPerteneceGrupoEtnico());
        datosEstadistica.setTieneReligion(personaDetalle.getTieneReligion());
        datosEstadistica.setTieneDependientes(personaDetalle.getTieneDependientes());
        datosEstadistica.setHablaLenguaExtranjera(personaDetalle.getHablaLenguaExtranjera());
        datosEstadistica.setLenguaExtranjera(personaDetalle.getLenguaExtranjera());
        datosEstadistica.setPerteneceGrupoVulnerable(personaDetalle.getPerteneceGrupoVulnerable());
        personaDTOGet.setDatosEstadistica(datosEstadistica);

        return personaDTOGet;
    }

    @Transactional
    public void updateParticipante(Integer personaDocumentoId, PersonaDTO personaDTO) throws NotFoundException {
        PersonaDocumento personaDocumento = personaDocumentoRepository.findById(personaDocumentoId)
        .orElseThrow(() -> new NotFoundException("PersonaDocumento no encontrado", "id"));

        PersonaDetalle personaDetalle = personaDetalleRepository.findByPersonaDocumentoId(personaDocumentoId)
        .orElseThrow(() -> new NotFoundException("PersonaDocumento no encontrado", ""));

        Domicilio domicilio = domicilioRepository.findById(personaDetalle.getDomicilio().getId())
        .orElseThrow(() -> new NotFoundException("PersonaDocumento no encontrado", ""));

        Integer tipoParteId = personaDTO.getDatosGenerales().getTipo();
        TipoPartes tipoParte = tipoPartesRepository.findById(tipoParteId)
            .orElseThrow(() -> new EntityNotFoundException("Tipo de partes no encontrado")); 

        
        if(!personaDTO.getDatosGenerales().getNombres().isEmpty()){
            personaDocumento.setNombre(personaDTO.getDatosGenerales().getNombres());
        } else if (!personaDTO.getDatosGenerales().getRazonSocial().isEmpty()){
            personaDocumento.setNombre(personaDTO.getDatosGenerales().getRazonSocial());
        }
        
        personaDocumento.setApellidoPaterno(personaDTO.getDatosGenerales().getApellidoPaterno());
        personaDocumento.setApellidoMaterno(personaDTO.getDatosGenerales().getApellidoMaterno());
        personaDocumento.setPseudonimo(personaDTO.getDatosGenerales().getPseudonimo());
        personaDocumento.setTipoPersona(personaDTO.getDatosGenerales().getTipoPersona());
        personaDocumento.setTipoPartes(tipoParte);
        personaDocumento.setCurp(personaDTO.getDatosGenerales().getCurp());
        personaDocumento.setCelular(personaDTO.getDatosContacto().getTelefono());  
        personaDocumento.setCorreoElectronico(personaDTO.getDatosContacto().getCorreoElectronico());

        domicilio.setCalle(personaDTO.getDatosContacto().getCalle());
        domicilio.setInterior(personaDTO.getDatosContacto().getNumeroInterior());
        domicilio.setExterior(personaDTO.getDatosContacto().getNumeroExterior());
        domicilio.setColonia(personaDTO.getDatosContacto().getColonia());
        domicilio.setCodigoPostal(personaDTO.getDatosContacto().getCodigoPostal());
        domicilio.setMunicipio(personaDTO.getDatosContacto().getMunicipio());
        domicilio.setEstadoRepublica(personaDTO.getDatosContacto().getEstado());

        if( personaDTO.getDatosContacto().getPais() != null){
            Integer idPaisResidencia = personaDTO.getDatosContacto().getPais();
            Pais paisResidencia = paisRepository.findById(idPaisResidencia).orElseThrow(() -> new EntityNotFoundException("Pais de residencia no encontrado"));  
            domicilio.setPaisResidencia(paisResidencia);
        } 

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
    
        if (personaDTO.getDatosEstadistica().getDocumentoIdentificacion() != null) {
            DocumentoIdentificacion documentoIdentificacion = documentoIdentificacionRepository.findById(personaDTO.getDatosEstadistica().getDocumentoIdentificacion())
                .orElseThrow(() -> new EntityNotFoundException("Documento Identificación no encontrado"));
            personaDetalle.setDocumentoIdentificacion(documentoIdentificacion);
        } else {
            personaDetalle.setDocumentoIdentificacion(null);
        }
       
        if (personaDTO.getDatosGenerales().getEnRepresentacion() != null) {
            PersonaDocumento enRepresentacion = personaDocumentoRepository.findById(personaDTO.getDatosGenerales().getEnRepresentacion())
                .orElseThrow(() -> new EntityNotFoundException("Persona representada no encontrada"));
            personaDetalle.setEnRepresentacionDe(enRepresentacion);
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
       
        personaDetalle.setMunicipioNacimiento(personaDTO.getDatosEstadistica().getMunicipioNacimiento());
        personaDetalle.setEntidadNacimiento(personaDTO.getDatosEstadistica().getEntidadNacimiento());
        personaDetalle.setEdad(personaDTO.getDatosGenerales().getEdad());

        if(personaDTO.getDatosEstadistica().getPaisNacimiento() != null){
            Integer idPaisNacimiento = personaDTO.getDatosEstadistica().getPaisNacimiento();
            Pais paisNacimiento = paisRepository.findById(idPaisNacimiento).orElseThrow(() -> new EntityNotFoundException("Pais de nacimiento no encontrado"));
            personaDetalle.setPaisNacimiento(paisNacimiento);
        }

        personaDetalle.setLenguaExtranjera(personaDTO.getDatosEstadistica().getLenguaExtranjera());
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

        personaDetalle.setTipoDomicilio(personaDTO.getDatosContacto().getTipoDomicilio());
        
        personaDetalle.setDiscapacidad(personaDTO.getDatosEstadistica().getDiscapacidad());
        
        personaDetalle.setEstadoCivil(personaDTO.getDatosGenerales().getEstadoCivil());

        personaDetalle.setCondicionMigratoria(personaDTO.getDatosEstadistica().getCondicionMigratoria());
        
        personaDetalle.setGrupoVulnerable(personaDTO.getDatosEstadistica().getGrupoVulnerable());
        
        personaDetalle.setProfesionOficio(personaDTO.getDatosEstadistica().getProfesion());
    
        personaDetalle.setIngresoMensualNeto(personaDTO.getDatosEstadistica().getIngresosMensuales());
       
        personaDetalle.setFrecuenciaIngreso(personaDTO.getDatosEstadistica().getFrecuenciaIngreso());

        personaDetalle.setTipoDefensor(personaDTO.getDatosGenerales().getTipoDefensor());
        
        personaDetalle.setRfc(personaDTO.getDatosGenerales().getRfc());
        
   
        personaDetalle.setHablaEspanol(personaDTO.getDatosEstadistica().getHablaEspanol());

        personaDetalle.setRecibePercepciones(personaDTO.getDatosEstadistica().getRecibePercepciones());

        personaDetalle.setHablaLenguaIndigena(personaDTO.getDatosEstadistica().getHablaLenguaIndigena());
       
        personaDetalle.setPerteneceGrupoVulnerable(personaDTO.getDatosEstadistica().getPerteneceGrupoVulnerable());
       
        personaDetalle.setTieneBienes(personaDTO.getDatosEstadistica().getTieneBienes());

        personaDetalle.setTieneSeniasParticulares(personaDTO.getDatosEstadistica().getTieneSeniasParticulares());

        personaDetalle.setPerteneceGrupoEtnico(personaDTO.getDatosEstadistica().getPerteneceGrupoEtnico());
       
        personaDetalle.setTieneReligion(personaDTO.getDatosEstadistica().getTieneReligion());
       
        personaDetalle.setTieneDependientes(personaDTO.getDatosEstadistica().getTieneDependientes());
       
        personaDetalle.setHablaLenguaExtranjera(personaDTO.getDatosEstadistica().getHablaLenguaExtranjera()); 

       
        personaDocumentoRepository.save(personaDocumento);
        domicilioRepository.save(domicilio);
        personaDetalleRepository.save(personaDetalle);
    }

    public List<TipoNotificacionRecord> getAllTiposNotificacion() {
        return Arrays.stream(TipoNotificacion.values())
        .map(tipo -> new TipoNotificacionRecord(
                tipo.ordinal(),
                tipo.name(),
                tipo.getTipoNotificacion() 
        ))
        .collect(Collectors.toList());
    }
}
