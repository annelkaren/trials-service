package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.salas.SalaSetUp;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.util.enums.Estado;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudienciaServiceTest {

    @Mock
    private AudienciaRepository audienciaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private BloqueRepository bloqueRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

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
        when(etiquetaRepository.findByTipoJuicioIdAndNombre(tipoJuicio.getId(),"domicilioOralidadFamiliar")).thenReturn(tipoJuicioEtiqueta);

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

}
