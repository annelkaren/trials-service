package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class MovimientosServiceTest {

    @Mock
    MovimientoRepository movimientoRepository;
    
    @Mock
    PersonaService personaService;
    @Mock
    DocumentoRepository documentoRepository;

    @Mock
    CarpetaRepository carpetaRepository;

    @InjectMocks
    MovimientoService movimientoService;

    MovimientoSalidaRecord movimiento;
    UUID uuid;

    EstadoCarpeta estadoCarpeta;

    @BeforeEach
    public void setUp() {
        String uuidMov = "d8945bc4-af8e-4eb0-b742-7ee13beb43e0";
        uuid = UUID.fromString(uuidMov);
        estadoCarpeta = EstadoCarpeta.TURNADO;

        movimiento = new MovimientoSalidaRecord(uuid, TipoCarpeta.DEMANDA, "1", "00001/2024", LocalDateTime.now(), "Juzgado 1", null, null);
    }

    @Test
    void getMovimientosSalidasTest(){
        List<MovimientoSalidaRecord> movimientos = List.of(movimiento);

        given(movimientoRepository.getSalidas(uuid, estadoCarpeta)).willReturn(movimientos);

        movimientos = movimientoService.getMovimientosSalida(uuid.toString());

        assertThat(movimientos)
            .isNotEmpty()
            .anyMatch(mov -> mov.uuid().equals(uuid));
    }

    @Test
    void createMovimiento(){
        Juzgado juzgado = JuzgadoSetUp.createJuzgado();
        Persona persona = PersonaSetUp.createPersona().setJuzgado(juzgado);
        Carpeta carpeta = CarpetaSetUp.create();
        Movimiento mov = new Movimiento()
                .setCarpeta(carpeta)
                .setDocumento(null)
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo("CAPTURA")
                .setPersona(persona)
                .setOficialia(null)
                .setJuzgado(juzgado);
        given(movimientoRepository.save(any(Movimiento.class))).willReturn(mov);

        Movimiento result = movimientoService.createMovimento(carpeta, null, persona, "", EstadoCarpeta.CAPTURA.name());
        assertThat(result.getCarpeta()).isEqualTo(carpeta);
        assertThat(result.getDocumento()).isEqualTo(null);
        assertThat(result.getFechaAsignacion()).isEqualTo(mov.getFechaAsignacion());
    }

    @Test
    void getAllBandejaRecepcion(){
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1)).setFolio("1");
        Movimiento movimiento = new Movimiento().setDocumento(demanda).setMotivo("RECEPCION");
        List<EstadoCarpeta> list = Arrays.asList(EstadoCarpeta.TURNADO, EstadoCarpeta.RECEPCION);
        List<String> motivos = Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.RECEPCION.name());
        given(movimientoRepository.getAllBandejaRecepcion(
                PageRequest.of(0, 1),
                1, list, "", motivos))
                .willReturn(new PageImpl<>(Arrays.asList(movimiento), PageRequest.of(0, 1), 1));


        Page<Movimiento> result = movimientoService.getAllBandejaRecepcion(PageRequest.of(0, 1),
                1, list, "", motivos);
        assertThat(result.getSize()).isPositive();
    }

    @Test
    void testCreateMotivoWithPromocion() {
        MotivoRecord motivoRecord = new MotivoRecord("Piezas Innecesarias", 2);
        Persona currentUser = new Persona();
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.PROMOCION);

        given(personaService.getAuditor()).willReturn(currentUser);
        given(documentoRepository.findById(motivoRecord.documentoId())).willReturn(Optional.of(documento));

        movimientoService.createMotivo(motivoRecord);

        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
        verify(documentoRepository, times(1)).actualizarEstatus(documento.getId(), EstadoCarpeta.DEVUELTO);
    }

    @Test
    void testCreateMotivoWithoutPromocion() {
        MotivoRecord motivoRecord = new MotivoRecord("Pase económico", 3);
        Persona currentUser = new Persona();
        Carpeta carpeta = new Carpeta();
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
        Documento documento = new Documento();  
        documento.setCarpeta(carpeta);  

        given(personaService.getAuditor()).willReturn(currentUser);
        given(documentoRepository.findById(motivoRecord.documentoId())).willReturn(Optional.of(documento));

        movimientoService.createMotivo(motivoRecord);

        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
        verify(carpetaRepository, times(1)).actualizarEstatus(carpeta.getId(), EstadoCarpeta.DEVUELTO);
    }

    @Test
    void createMovimentoWithObservaciones(){
        Juzgado juzgado = JuzgadoSetUp.createJuzgado();
        Persona persona = PersonaSetUp.createPersona().setJuzgado(juzgado);
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        Movimiento mov = new Movimiento()
                .setCarpeta(null)
                .setDocumento(documento)
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo("RECEPCION")
                .setPersona(persona)
                .setOficialia(null)
                .setJuzgado(juzgado);
        given(movimientoRepository.save(any(Movimiento.class))).willReturn(mov);

        Movimiento result = movimientoService.createMovimento(null, documento, persona, "", EstadoCarpeta.RECEPCION.name());
        assertThat(result.getCarpeta()).isEqualTo(null);
        assertThat(result.getDocumento()).isEqualTo(documento);
        assertThat(result.getFechaAsignacion()).isEqualTo(mov.getFechaAsignacion());
    }
}