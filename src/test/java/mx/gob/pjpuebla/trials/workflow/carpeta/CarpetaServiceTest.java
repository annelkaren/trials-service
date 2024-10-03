package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CarpetaServiceTest {

    @Mock
    CarpetaRepository carpetaRepository;

    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;

    @InjectMocks
    CarpetaService target;
    @Mock
    TipoJuicioRepository tipoJuicioRepository;
    @Mock
    AnexoRepository anexoRepository;
    @Mock
    private JuzgadoService juzgadoService;
    @Mock
    TipoPartesRepository tipoPartesRepository;
    @Mock
    private TipoSistemaRepository tipoSistemaRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    DocumentoService documentoService;
    @InjectMocks
    CarpetaService carpetaService;
    @Mock
    DocumentoRepository documentoRepository;
    @Mock
    private DistritoRepository distritoRepository;
    @Mock
    private DomicilioRepository domicilioRepository;
    @Mock
    private SedeRepository sedeRepository;
    @Mock
    private JuzgadoRepository juzgadoRepository;


    private Carpeta validCarpeta;
    private PersonaDocumentoRecord actor;
    private PersonaDocumentoRecord demandado;
    private ApelacionRecordResponse apelacionRecordResponse;
    private TipoJuicio tipoJuicio;
    private Juzgado juzgado;
    private JuzgadoFolios juzgadoFolios;
    private TipoPartes actorApelacion;
    private TipoPartes demandadoApelacion;


    @BeforeEach
    public void setUp() {
        validCarpeta = CarpetaSetUp.create(TipoJuicioSetUp.createTipoJuicio(), JuzgadoSetUp.createJuzgado());
        actor = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "Actor", 1, 200);
        demandado = new PersonaDocumentoRecord("Nauj", "Zerep", "", null, "fisica", "Demandado", 1, 200);
        apelacionRecordResponse = CarpetaSetUp.apelacionRecordResponse();
        actorApelacion = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio);
        tipoPartesRepository.save(actorApelacion);
        demandadoApelacion = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio).setNombre("Demandado");
        tipoPartesRepository.save(demandadoApelacion);

        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());

        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgado.setContadorAsignaciones(0);
        juzgado.setMaxAsignacionesRonda(0);

        juzgadoFolios = createJuzgadoFolios();
        juzgadoFolios.setJuzgado(juzgado);
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_CarpetaResponseRecord() {
        given(carpetaRepository.findByExpedienteAndJuzgadoId(any(), any()))
                .willReturn(Optional.ofNullable(validCarpeta));
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any()))
                .willReturn(actor);
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any()))
                .willReturn(demandado);

        CarpetaResponseRecord carpetaResponseRecord = target.getCarpetaResponseByNumExpYearJuzgado(any(), any());

        assertThat(carpetaResponseRecord)
                .isOfAnyClassIn(CarpetaResponseRecord.class)
                .hasFieldOrPropertyWithValue("idCarpeta", validCarpeta.getId())
                .hasFieldOrPropertyWithValue("actor", actor.nombre() + " " + actor.apellidoPaterno())
                .hasFieldOrPropertyWithValue("demandado", demandado.nombre() + " " + demandado.apellidoPaterno());
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_not_found() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.getCarpetaResponseByNumExpYearJuzgado("1", 1);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Carpeta no encontrada");
    }

    @Test
    void getPersonaDocumentoById_return_carpetaId() {
        List<ApelacionRecordResponse> expectedResponses = Collections.singletonList(apelacionRecordResponse);

        given(carpetaRepository.findPersonaDocumentoByCarpetaId(validCarpeta.getId()))
                .willReturn(expectedResponses);

        List<ApelacionRecordResponse> result = target.getPersonasDocumentoByCarpetaId(validCarpeta.getId());
        assertThat(result).isNotEmpty();
        ApelacionRecordResponse actualResponse = result.get(0);
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(apelacionRecordResponse);
    }

    @Test
    void create_Apelacion() {
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        Carpeta carpetaMock = CarpetaSetUp.create();
        demanda.setCarpeta(carpetaMock);
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.APELACION);
        TipoPartes tipoPartesMock = new TipoPartes();

        lenient().when(carpetaRepository.findById(carpetaMock.getId())).thenReturn(Optional.of(carpetaMock));
        lenient().when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(tipoJuicio));
        lenient().when(juzgadoService.getConexidadJuzgado(any(), any(), any())).thenReturn(new Juzgado());
        lenient().when(carpetaRepository.findById(carpetaMock.getId())).thenReturn(Optional.of(carpetaMock));
        lenient().when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(tipoJuicio));
        given(documentoService.generateNumExpediente(any(), eq(TipoCarpeta.APELACION))).willReturn("E000001/2024");
        given(tipoPartesRepository.findById(any())).willReturn(Optional.of(tipoPartesMock));
        given(anexoRepository.save(any())).willReturn(new Anexo());
        given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta());
        given(juzgadoRepository.findById(51)).willReturn(Optional.of(new Juzgado()));
        given(documentoRepository.save(any(Documento.class))).willReturn(demanda);

        ApelacionRecord apelacionRecord = CarpetaSetUp.apelacionRecord();

        DocumentoRecord response = carpetaService.createApelacion(apelacionRecord);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", demanda.getId())
                .hasFieldOrPropertyWithValue("folio", demanda.getCarpeta().getFolio())
                .hasFieldOrPropertyWithValue("tipoCarpeta", TipoCarpeta.APELACION);
        verify(carpetaRepository).findById(apelacionRecord.carpetaId());
        verify(tipoJuicioRepository).findById(demanda.getCarpeta().getTipoJuicio().getId());
        verify(juzgadoRepository).findById(51);
        verify(carpetaRepository).save(any(Carpeta.class));
        verify(documentoRepository).save(any(Documento.class));
        verify(anexoRepository).save(any(Anexo.class));
        verify(personaDocumentoRepository, times(apelacionRecord.apelacionPersonaRecords().size())).save(any(PersonaDocumento.class));
    }
}