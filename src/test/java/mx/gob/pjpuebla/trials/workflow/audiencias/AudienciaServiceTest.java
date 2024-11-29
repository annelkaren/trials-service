package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.salas.SalaSetUp;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.util.enums.CatalogoMotivosRetrasoAudiencias;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.*;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.etiquetas.Etiqueta;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaRepository;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudienciaServiceTest {

    @Mock
    private AudienciaRepository audienciaRepository;

    @Mock
    private TipoAudienciaRepository tipoAudienciaRepository;

    @Mock
    private CarpetaRepository carpetaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private BloqueRepository bloqueRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private AudienciaService audienciaService;

    private SalaAudienciaRecord salaAudiencia;
    private Sala sala;
    private Bloque bloque;
    private TipoAudiencia tipoAudiencia;
    private Carpeta carpeta;
    private Documento documento;
    private Persona persona;
    private TipoJuicio tipoJuicio;
    private Etiqueta tipoJuicioEtiqueta;
    

    @BeforeEach
    public void setUp() {
        sala = SalaSetUp.createSala_juez();
        bloque = BloqueSetUp.createBloque();
        tipoAudiencia = new TipoAudiencia().setNombre("Audiencia Inicial").setId(1);
        carpeta = CarpetaSetUp.createOralidadFamiliar();
        salaAudiencia = SalaSetUp.salaAudienciaRecord();
        tipoJuicio = TipoJuicioSetUp.createTipoJuicioOralFamiliar();
        persona = PersonaSetUp.createPersona();
        documento = DocumentoSetUp.create_data(tipoJuicio);
        tipoJuicioEtiqueta = EtiquetaSetUp.createEtiqueta(tipoJuicio.getId());

    }

    @Test
    void createAudiencia_SaveAudiencia() {

        when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));
        when(bloqueRepository.findById(salaAudiencia.bloqueId())).thenReturn(Optional.of(bloque));

        Audiencia savedAudiencia = AudienciaSetUp.generarAudiencia(
                salaAudiencia.fechaAudiencia(),
                sala,
                bloque,
                tipoAudiencia,
                carpeta);

        when(audienciaRepository.save(any(Audiencia.class))).thenReturn(savedAudiencia);

        Audiencia result = audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);

        assertThat(result).isNotNull();
        verify(audienciaRepository).save(any(Audiencia.class));

        assertThat(result.getFechaAudiencia()).isEqualTo(salaAudiencia.fechaAudiencia());
        assertThat(result.getSala()).isEqualTo(sala);
        assertThat(result.getBloque()).isEqualTo(bloque);
        assertThat(result.getTipoAudiencia()).isEqualTo(tipoAudiencia);
        assertThat(result.getCarpeta()).isEqualTo(carpeta);
        assertThat(result.getEstatusAudiencia()).isEqualTo(EstatusAudiencia.PROGRAMADA);
        assertThat(result.getEstado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void DomicilioByaudiencias() {
        AudienciaOralidadFamiliarRecord audiencia = new AudienciaOralidadFamiliarRecord(
                persona.getNombre(),
                persona.getApellidoPaterno(),
                persona.getApellidoMaterno(),
                sala.getNombre(),
                carpeta.getTipoJuicio().getNombre(),
                LocalDateTime.now()
        );

        when(audienciaRepository.getJuzAndSalaAndAudienciaByIdcarpeta(carpeta.getId())).thenReturn(audiencia);
        when(etiquetaRepository.findByTipoJuicioIdAndNombre(tipoJuicio.getId(), "domicilioOralidadFamiliar")).thenReturn(tipoJuicioEtiqueta);

        ExtraAudienciaSelloRecord entity = audienciaService.getAudienciaAndSalaAndDomicilio(documento);

        assertThat(entity).isNotNull();
        assertThat(entity.nombreJuez()).isEqualTo("Juan Perez ");
        assertThat(entity.nombreSala()).isEqualTo("1");
        assertThat(entity.nombreTipoJuicio()).isEqualTo("Familiar Oralidad (Alimentos)");
        assertThat(entity.domicilio()).isEqualTo("Demanda:<b> Example Domicilio</b>");
    }


    @Test
    void DomicilioByaudiencias_isEmpty() {
        when(audienciaRepository.getJuzAndSalaAndAudienciaByIdcarpeta(carpeta.getId())).thenReturn(null);
        when(etiquetaRepository.findByTipoJuicioIdAndNombre(tipoJuicio.getId(), "domicilioOralidadFamiliar")).thenReturn(null);

        ExtraAudienciaSelloRecord entity = audienciaService.getAudienciaAndSalaAndDomicilio(documento);
        assertThat(entity).isNotNull();
        assertThat(entity.nombreJuez()).isEmpty();
        assertThat(entity.nombreSala()).isEmpty();
        assertThat(entity.nombreTipoJuicio()).isEmpty();
        assertThat(entity.domicilio()).isEmpty();
    }

    @Test
    void getAllAudienciasGenerales_ReturnPage() {
        Juzgado juzgado = new Juzgado();
        persona.setJuzgado(juzgado);
        when(personaService.getAuditor()).thenReturn(persona);

        Persona juez = new Persona();
        juez.setNombre("Juez 1");
        juez.setApellidoPaterno("");
        juez.setApellidoMaterno("");
        Sala sala = new Sala().setNombre("Sala 1").setJuez(juez);

        String nombreCompletoJuez = juez.getNombre() + " " + juez.getApellidoPaterno();
        if (juez.getApellidoMaterno() != null) {
            nombreCompletoJuez += " " + juez.getApellidoMaterno();
        }

        Carpeta carpeta = new Carpeta().setExpediente("000001/2024");
        Audiencia audiencia = AudienciaSetUp.generarAudiencia(LocalDateTime.now(), sala, null, tipoAudiencia, carpeta);

        List<Audiencia> audiencias = Collections.singletonList(audiencia);
        Page<Audiencia> pageAudiencias = new PageImpl<>(audiencias, PageRequest.of(0, 10), audiencias.size());
        when(audienciaRepository.findByJuzgado(eq(juzgado), anyString(), any(Pageable.class)))
                .thenReturn(pageAudiencias);

        Page<AudienciasGeneralesResponseRecord> result = audienciaService.getAllAudienciasGenerales("", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", audiencia.getId())
                .hasFieldOrPropertyWithValue("tipoAudiencia", tipoAudiencia.getNombre())
                .hasFieldOrPropertyWithValue("juez", nombreCompletoJuez)
                .hasFieldOrPropertyWithValue("lugar", sala.getNombre())
                .hasFieldOrPropertyWithValue("numCarpeta", carpeta.getExpediente())
                .hasFieldOrPropertyWithValue("fechaHora", audiencia.getFechaAudiencia())
                .hasFieldOrPropertyWithValue("estatus", audiencia.getEstatusAudiencia());
    }

    @Test
    void deleteAudiencia() {
        Integer audienciaId = 1;
        Audiencia audiencia = new Audiencia();
        audiencia.setId(audienciaId);
        audiencia.setEstado(Estado.ACTIVE);

        when(audienciaRepository.findById(audienciaId)).thenReturn(Optional.of(audiencia));
        doThrow(DataIntegrityViolationException.class).when(audienciaRepository).save(audiencia);
        assertThrows(ConstraintViolationException.class, () -> audienciaService.deleteAudiencia(audienciaId));
    }

    @Test
    void getAudienciasMotivos() {
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoMotivosRetrasoAudiencias.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();

        List<CarpetaCatalogoRecord> result = audienciaService.getAudienciasMotivos();

        assertNotNull(result);
        assertEquals(result.size(), CatalogoMotivosRetrasoAudiencias.values().length);
        assertThat(result).isEqualTo(items);
    }

    @Test
    void diferirAudiencia_success() {
        Audiencia audiencia = new Audiencia();
        audiencia.setId(1);
        audiencia.setFechaAudiencia(LocalDateTime.now());
        audiencia.setEstatusAudiencia(EstatusAudiencia.PROGRAMADA);

        when(audienciaRepository.findById(1)).thenReturn(Optional.of(audiencia));

        audienciaService.diferirAudiencia(1);

        assertNull(audiencia.getFechaAudiencia());
        assertEquals(EstatusAudiencia.DIFERIDA, audiencia.getEstatusAudiencia());
        verify(audienciaRepository).save(audiencia);
    }

    @Test
    void createAudiencia() {
        Audiencia audiencia = new Audiencia();
        audiencia.setId(1);
        audiencia.setEstatusAudiencia(EstatusAudiencia.PROGRAMADA);
    
        given(salaRepository.findById(anyInt())).willReturn(Optional.of(sala));
        given(tipoAudienciaRepository.findById(anyInt())).willReturn(Optional.of(tipoAudiencia));
        given(carpetaRepository.findById(anyInt())).willReturn(Optional.of(carpeta));
        given(audienciaRepository.save(any(Audiencia.class))).willAnswer(invocation -> {
            Audiencia saved = invocation.getArgument(0);
            saved.setId(1); 
            return saved;
        });
    
        AudienciasResponseRecord response = audienciaService.createAudiencia(AudienciaSetUp.audienciaSaveRecordCreate());
    
        assertEquals(1, response.audienciaId());
        assertEquals(EstatusAudiencia.PROGRAMADA, response.estatus());
    }

    @Test
    void testGetEstatusAudiencias() {
        List<String> result = audienciaService.getEstatusAudiencias();
        assertNotNull(result);

        for (EstatusAudiencia estatus : EstatusAudiencia.values()) {
            assertTrue(result.contains(estatus.name()));
        }
    }

    @Test
    void testSetHoraAudiencias_success_inicio() {
        LocalDateTime horaInicio = LocalDateTime.now();
        Audiencia audiencia = new Audiencia();
        audiencia.setId(1);

        when(audienciaRepository.findById(1)).thenReturn(Optional.of(audiencia));

        audienciaService.setHoraAudiencias(1, horaInicio, true);

        assertEquals(horaInicio, audiencia.getInicio());
    }

    @Test
    void testSetHoraAudiencias_success_fin() {
        LocalDateTime horaFin = LocalDateTime.now();
        Audiencia audiencia = new Audiencia();
        audiencia.setId(1);

        when(audienciaRepository.findById(1)).thenReturn(Optional.of(audiencia));

        audienciaService.setHoraAudiencias(1, horaFin, false);

        assertEquals(horaFin, audiencia.getFin());
    }

    @Test
    void testAudienciaTabGeneral() {
        AudienciaTabGeneralRecord audienciaTab = AudienciaSetUp.createAudienciaTabGeneralRecord();

        Audiencia audiencia = new Audiencia();
        audiencia.setId(1);
        audiencia.setEstatusAudiencia(EstatusAudiencia.PROGRAMADA);

        TipoAudiencia tipoAudiencia = new TipoAudiencia();
        tipoAudiencia.setId(1);

        Sala sala = new Sala();
        sala.setId(1);

        when(audienciaRepository.findById(1)).thenReturn(Optional.of(audiencia));
        when(tipoAudienciaRepository.findById(1)).thenReturn(Optional.of(tipoAudiencia));
        when(salaRepository.findById(1)).thenReturn(Optional.of(sala));

        audienciaService.audienciaTabGeneral(audienciaTab);

        assertEquals(tipoAudiencia, audiencia.getTipoAudiencia());
        assertEquals(sala, audiencia.getSala());
        assertEquals("PROGRAMADA", audiencia.getEstatusAudiencia().name());

        assertEquals(CatalogoMotivosRetrasoAudiencias.RETRASO_AUDIENCIA, audiencia.getMotivoRetrasoAudiencias());
        assertEquals("Otro", audiencia.getResultadosDesahogo());

        verify(audienciaRepository).save(audiencia);
    }


}
