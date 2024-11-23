package mx.gob.pjpuebla.trials.workflow.personadetalle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigena;
import mx.gob.pjpuebla.trials.core.lenguasindigenas.LenguaIndigenaRepository;
import mx.gob.pjpuebla.trials.core.nacionalidades.Nacionalidad;
import mx.gob.pjpuebla.trials.core.nacionalidades.NacionalidadRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacion;
import mx.gob.pjpuebla.trials.core.documentoidentificacion.DocumentoIdentificacionRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PersonaDTO createMockPersonaDTO() {
    // Datos generales
    PersonaDTO.DatosGenerales datosGenerales = new PersonaDTO.DatosGenerales();
    datosGenerales.setTipo(List.of("Actor"));
    datosGenerales.setNombre("Susana");
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
    datosGenerales.setTipoDefensor("");
    datosGenerales.setAdscripcion("");
    datosGenerales.setEnRepresentacion("");
    datosGenerales.setIdCarpeta(1);

    PersonaDTO.DatosContacto datosContacto = new PersonaDTO.DatosContacto();
    datosContacto.setPais("484");
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
    datosEstadistica.setPaisNacimiento("Estados Unidos Mexicanos");
    datosEstadistica.setCondicionMigratoria("NO_APLICA");
    datosEstadistica.setLenguaExtranjeraDetalle("");
    datosEstadistica.setProfesion("VENTAS");
    datosEstadistica.setEntidadNacimiento("Puebla");
    datosEstadistica.setLenguaIndigena(11);
    datosEstadistica.setGrupoVulnerable("MUJERES");
    datosEstadistica.setRecibePercepciones(false);
    datosEstadistica.setDiscapacidad("");
    datosEstadistica.setMunicipioNacimiento("Ahuatlán");
    datosEstadistica.setSabeLeer(true);
    datosEstadistica.setIngresosMensuales("");
    datosEstadistica.setFrecuenciaIngreso("");
    datosEstadistica.setLugarTrabajo("Oficina");
    datosEstadistica.setHablaEspanol(true);
    datosEstadistica.setSenias("");
    datosEstadistica.setGrupoEtnico("");
    datosEstadistica.setDatosPrivados(true);
    datosEstadistica.setDetallesDependientes("");
    datosEstadistica.setTieneDependientes(false);
    datosEstadistica.setDocumento(50);
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
        mockTipoPartes.setNombre("Actor");

        Nacionalidad mockNacionalidad = new Nacionalidad();

        Escolaridad mockEscolaridad = new Escolaridad();
        mockEscolaridad.setId(1);
        mockEscolaridad.setNombre("Primaria");

        LenguaIndigena mockLenguaIndigena = new LenguaIndigena();
        mockLenguaIndigena.setId(11);
        mockLenguaIndigena.setName("Nahuatl");

        DocumentoIdentificacion mockDocumentoIdentificacion = new DocumentoIdentificacion();
        mockDocumentoIdentificacion.setId(50);
        mockDocumentoIdentificacion.setName("CRedencial de elector");

        when(carpetaRepository.findById(1)).thenReturn(Optional.of(mockCarpeta));
        when(tipoPartesRepository.findByNombreAndTipoJuicioId("Actor", 1)).thenReturn(Optional.of(mockTipoPartes));
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

    }
}
