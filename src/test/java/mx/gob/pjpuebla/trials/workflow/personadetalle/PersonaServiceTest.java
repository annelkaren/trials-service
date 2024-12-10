package mx.gob.pjpuebla.trials.workflow.personadetalle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigena;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigenaRepository;
import mx.gob.pjpuebla.trials.core.nacionalidades.Nacionalidad;
import mx.gob.pjpuebla.trials.core.nacionalidades.NacionalidadRepository;
import mx.gob.pjpuebla.trials.core.paises.Pais;
import mx.gob.pjpuebla.trials.core.paises.PaisRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTO;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTOGet;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PersonaDetalleServiceTest {

    @Mock
    private PersonaDocumentoRepository personaDocumentoRepository;
    @Mock
    private CarpetaRepository carpetaRepository;
    @Mock
    private TipoPartesRepository tipoPartesRepository;
    @Mock
    private LenguaIndigenaRepository lenguaIndigenaRepository;
    @Mock
    private NacionalidadRepository nacionalidadRepository;
    @Mock
    private DocumentoIdentificacionRepository documentoIdentificacionRepository;
    @Mock
    private EscolaridadRepository escolaridadRepository;
    @Mock
    private DomicilioRepository domicilioRepository;
    @Mock
    private PersonaDetalleRepository personaDetalleRepository;

    @InjectMocks
    private PersonaDetalleService personaDetalleService;

    @Mock
    private PaisRepository paisRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PersonaDTO createMockPersonaDTO() {
    // Datos generales
    PersonaDTO.DatosGenerales datosGenerales = new PersonaDTO.DatosGenerales();
    datosGenerales.setTipo(200);
    datosGenerales.setNombres("Susana");
    datosGenerales.setPseudonimo("");
    datosGenerales.setNacionalidad(11);
    datosGenerales.setRfc("REGU050806J7G");
    datosGenerales.setApellidoPaterno("Reyes");
    datosGenerales.setSexo("Femenino");
    datosGenerales.setEdad(25);
    datosGenerales.setCurp("REGU050806MPLYRNA3");
    datosGenerales.setTipoPersona("Fisica");
    datosGenerales.setApellidoMaterno("Guitierrez");
    datosGenerales.setFechaNacimiento(LocalDate.of(1999, 2, 9));
    datosGenerales.setEstadoCivil("SOLTERO");
    datosGenerales.setRazonSocial("");
    datosGenerales.setCedula("");
    datosGenerales.setTipoDefensor(null);
    datosGenerales.setAdscripcion("");
    datosGenerales.setEnRepresentacion(null);
    datosGenerales.setIdCarpeta(1);

    PersonaDTO.DatosContacto datosContacto = new PersonaDTO.DatosContacto();
    datosContacto.setPais(25);
    datosContacto.setEstado("Puebla");
    datosContacto.setMunicipio("Albino Zertuche");
    datosContacto.setCodigoPostal("23453");
    datosContacto.setColonia("Bugambilias");
    datosContacto.setCalle("5 sur");
    datosContacto.setNumeroExterior("5");
    datosContacto.setNumeroInterior("1334");
    datosContacto.setTipoDomicilio("PARTICULAR");
    datosContacto.setCorreoElectronico("susana@gmail.com");
    datosContacto.setTelefono("2225647890");

    PersonaDTO.DatosEstadistica datosEstadistica = new PersonaDTO.DatosEstadistica();
    datosEstadistica.setEscolaridad(1);
    datosEstadistica.setPaisNacimiento(25);
    datosEstadistica.setCondicionMigratoria("NO_APLICA");
    datosEstadistica.setLenguaExtranjera("");
    datosEstadistica.setProfesion("VENTAS");
    datosEstadistica.setEntidadNacimiento("Puebla");
    datosEstadistica.setLenguaIndigena(11);
    datosEstadistica.setGrupoVulnerable("MUJERES");
    datosEstadistica.setRecibePercepciones(false);
    datosEstadistica.setDiscapacidad(null);
    datosEstadistica.setMunicipioNacimiento("Ahuatlán");
    datosEstadistica.setSabeLeer(true);
    datosEstadistica.setIngresosMensuales(null);
    datosEstadistica.setFrecuenciaIngreso(null);
    datosEstadistica.setLugarTrabajo("Oficina");
    datosEstadistica.setHablaEspanol(true);
    datosEstadistica.setSenias("");
    datosEstadistica.setGrupoEtnico("");
    datosEstadistica.setDatosPrivados(true);
    datosEstadistica.setDetallesDependientes("");
    datosEstadistica.setTieneDependientes(false);
    datosEstadistica.setDocumentoIdentificacion(50);
    datosEstadistica.setDiscapacidad("NO_SABE");
    datosEstadistica.setCantidadBienes(1);
    datosEstadistica.setReligion("Catolica");

    PersonaDTO personaDTO = new PersonaDTO();
    personaDTO.setDatosGenerales(datosGenerales);
    personaDTO.setDatosContacto(datosContacto);
    personaDTO.setDatosEstadistica(datosEstadistica);

    return personaDTO;
    }

    PersonaDTO mockPersonaDTO = createMockPersonaDTO();

    @Test
    void testCreatePersonaDetalle_whenCarpetaNotFound_throwsEntityNotFoundException() {
        PersonaDTO mockPersonaDTO = createMockPersonaDTO();
        mockPersonaDTO.getDatosGenerales().setIdCarpeta(123);

        when(carpetaRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> personaDetalleService.createPersonaDetalle(mockPersonaDTO));

        assertEquals("Carpeta no encontrada", exception.getMessage());
        verify(carpetaRepository, times(1)).findById(123);
    }

    @Test
    void testCreatePersonaDetalle_whenValidInputs_savesPersonaDocumento() {
        PersonaDTO mockPersonaDTO = createMockPersonaDTO();
        mockPersonaDTO.getDatosGenerales().setIdCarpeta(1);

        Carpeta mockCarpeta = new Carpeta();
        TipoJuicio mockTipoJuicio = new TipoJuicio();
        mockTipoJuicio.setId(1);
        mockCarpeta.setTipoJuicio(mockTipoJuicio);

        TipoPartes mockTipoPartes = new TipoPartes();
        mockTipoPartes.setId(200);
        mockTipoPartes.setNombre("Actor"); 
        when(tipoPartesRepository.findById(200)).thenReturn(Optional.of(mockTipoPartes));

        Nacionalidad mockNacionalidad = new Nacionalidad();

        Escolaridad mockEscolaridad = new Escolaridad();
        mockEscolaridad.setId(1);
        mockEscolaridad.setNombre("Primaria");

        LenguaIndigena mockLenguaIndigena = new LenguaIndigena();
        mockLenguaIndigena.setId(11);
        mockLenguaIndigena.setName("Nahuatl");

        DocumentoIdentificacion mockDocumentoIdentificacion = new DocumentoIdentificacion();
        mockDocumentoIdentificacion.setId(50);
        mockDocumentoIdentificacion.setName("Credencial de elector");

        Pais mockPais = new Pais();
        mockPais.setId(25); 
        when(paisRepository.findById(25)).thenReturn(Optional.of(mockPais));

        when(carpetaRepository.findById(1)).thenReturn(Optional.of(mockCarpeta));
        when(nacionalidadRepository.findById(11)).thenReturn(Optional.of(mockNacionalidad));
        when(escolaridadRepository.findById(1)).thenReturn(Optional.of(mockEscolaridad));
        when(lenguaIndigenaRepository.findById(11)).thenReturn(Optional.of(mockLenguaIndigena));
        when(documentoIdentificacionRepository.findById(50)).thenReturn(Optional.of(mockDocumentoIdentificacion));

        personaDetalleService.createPersonaDetalle(mockPersonaDTO);

        verify(personaDocumentoRepository, times(1)).save(any());
        verify(domicilioRepository, times(1)).save(any());
        verify(escolaridadRepository, times(1)).findById(1);
        verify(lenguaIndigenaRepository, times(1)).findById(11);
        verify(documentoIdentificacionRepository, times(1)).findById(50);
        verify(paisRepository, times(2)).findById(25);
 
    }

    @Test
    void testGetParticipante_whenValidId_returnsPersonaDTOGet() {
        Integer id = 1;
        PersonaDocumento mockPersonaDocumento = new PersonaDocumento();
        mockPersonaDocumento.setId(id);
        mockPersonaDocumento.setApellidoPaterno("Reyes");
        mockPersonaDocumento.setNombre("Susana");
        mockPersonaDocumento.setCurp("REGU050806MPLYRNA3");
        mockPersonaDocumento.setTipoPersona("fisica");
        mockPersonaDocumento.setCorreoElectronico("susana@gmail.com");
        mockPersonaDocumento.setCelular("2225647890");

        TipoPartes mockTipoPartes = new TipoPartes();
        mockTipoPartes.setId(1);
        mockPersonaDocumento.setTipoPartes(mockTipoPartes); 
        
        Domicilio mockDomicilio = new Domicilio();
        mockDomicilio.setId(1L);
        mockDomicilio.setCalle("5 sur");
        mockDomicilio.setColonia("Bugambilias");
        mockDomicilio.setCodigoPostal("23453");
        mockDomicilio.setEstadoRepublica("Puebla");
        mockDomicilio.setExterior("5");
        mockDomicilio.setInterior("1334");

        PersonaDetalle mockPersonaDetalle = new PersonaDetalle();
        mockPersonaDetalle.setId(1);
        mockPersonaDetalle.setDomicilio(mockDomicilio); 
        mockPersonaDetalle.setRfc("REGU050806J7G");
        mockPersonaDetalle.setEdad(25);
        mockPersonaDetalle.setTipoDomicilio("PARTICULAR");


        when(personaDocumentoRepository.findById(id)).thenReturn(Optional.of(mockPersonaDocumento));
        when(personaDetalleRepository.findByPersonaDocumentoId(id)).thenReturn(Optional.of(mockPersonaDetalle));
        when(domicilioRepository.findById(1L)).thenReturn(Optional.of(mockDomicilio)); 

        PersonaDTOGet result = personaDetalleService.getParticipante(id);

        assertNotNull(result);
        assertEquals(id, result.getPersonaDocumentoId());
        assertEquals(1L, result.getDomicilioId()); 
        assertEquals(1, result.getPersonaDetalleId());

        verify(personaDocumentoRepository, times(1)).findById(id);
        verify(personaDetalleRepository, times(1)).findByPersonaDocumentoId(id);
        verify(domicilioRepository, times(1)).findById(1L); 
    }


    @Test
    void testUpdatePersonaDetalle_whenValidInputs_updatesPersonaDetalle() {
        Integer id = 1;
        PersonaDTO mockPersonaDTO = createMockPersonaDTO();
        mockPersonaDTO.getDatosGenerales().setIdCarpeta(1);
    
        Carpeta mockCarpeta = new Carpeta();
        TipoJuicio mockTipoJuicio = new TipoJuicio();
        mockTipoJuicio.setId(1);
        mockCarpeta.setTipoJuicio(mockTipoJuicio);
    
        Domicilio mockDomicilio = new Domicilio();
        mockDomicilio.setId(1L);
    
        PersonaDetalle mockPersonaDetalle = new PersonaDetalle();
        mockPersonaDetalle.setDomicilio(mockDomicilio);
    
        when(carpetaRepository.findById(1)).thenReturn(Optional.of(mockCarpeta));
        when(personaDetalleRepository.findByPersonaDocumentoId(id)).thenReturn(Optional.of(mockPersonaDetalle));
        when(personaDocumentoRepository.findById(id)).thenReturn(Optional.of(new PersonaDocumento()));
        when(nacionalidadRepository.findById(11)).thenReturn(Optional.of(new Nacionalidad()));
        when(escolaridadRepository.findById(1)).thenReturn(Optional.of(new Escolaridad()));
        when(lenguaIndigenaRepository.findById(11)).thenReturn(Optional.of(new LenguaIndigena()));
        when(documentoIdentificacionRepository.findById(50)).thenReturn(Optional.of(new DocumentoIdentificacion()));
        when(paisRepository.findById(25)).thenReturn(Optional.of(new Pais()));
        when(domicilioRepository.findById(1L)).thenReturn(Optional.of(mockDomicilio));
        when(tipoPartesRepository.findById(200)).thenReturn(Optional.of(new TipoPartes()));

        personaDetalleService.updateParticipante(id, mockPersonaDTO);
    
        verify(personaDocumentoRepository, times(1)).save(any()); 
        verify(domicilioRepository, times(1)).save(any());  
        verify(personaDetalleRepository, times(1)).save(any()); 
        verify(nacionalidadRepository, times(1)).findById(11);
        verify(escolaridadRepository, times(1)).findById(1);
        verify(lenguaIndigenaRepository, times(1)).findById(11);
        verify(documentoIdentificacionRepository, times(1)).findById(50);
        verify(paisRepository, times(2)).findById(25);
        verify(domicilioRepository, times(1)).findById(1L);
    }

}
