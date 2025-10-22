package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.persistence.EntityNotFoundException;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRepository;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoSetUp;
import mx.gob.pjpuebla.trials.core.configuraciones.ConfiguracionesRepository;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.eventos.EventoService;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionRepository;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioDemandasRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.EmailService;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoImpugnacionAmparo;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoSentidoAmparo;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoGetRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoUpdateRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.PromocionSinExpedienteService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaService;
import mx.gob.pjpuebla.trials.workflow.folios.DocumentoFoliosService;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalle;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas.SolicitudesProrrogasService;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

        @Mock
        private DocumentoRepository documentoRepository;
        @Mock
        private TipoJuicioRepository tipoJuicioRepository;
        @Mock
        private AnexoRepository anexoRepository;
        @InjectMocks
        private DocumentoService documentoService;
        @Mock
        private PersonaDocumentoRepository personaDocumentoRepository;
        @Mock
        private TipoPartesRepository tipoPartesRepository;
        @Mock
        private JuzgadoRepository juzgadoRepository;
        @Mock
        private SedeRepository sedeRepository;
        @Mock
        private MateriaRepository materiaRepository;
        @Mock
        private DistritoRepository distritoRepository;
        @Mock
        private DomicilioRepository domicilioRepository;
        @Mock
        private TipoSistemaRepository tipoSistemaRepository;
        @Mock
        private JuzgadoService juzgadoService;
        @Mock
        private CarpetaRepository carpetaRepository;
        @Mock
        private MovimientoService movimientoService;
        @Mock
        private MovimientoRepository movimientoRepository;
        @Mock
        private AuditorAware<Jwt> auditorAware;
        @Mock
        private PersonaService personaService;
        @Mock
        private PersonaRepository personaRepository;
        @Mock
        private EtiquetaService etiquetaService;
        @Mock
        private RoleService roleService;
        @Mock
        private InstitucionRepository institucionRepository;
        @Mock
        private DocumentoFoliosService documentoFoliosService;
        @Mock
        private ConceptoRepository conceptoRepository;
        @Mock
        private DocumentoDetalleRepository documentoDetalleRepository;
        @Mock
        private DocumentoContenidoRepository documentoContenidoRepository;
        @Mock
        private AudienciaRepository audienciaRepository;
        @Mock
        private SelloGenerator selloGenerator;
        @Mock
        private EmailService emailService;
        @Mock
        private DigitalizacionService digitalizacionService;
        @Mock
        private CarpetaService carpetaService;
        @Mock
        private CarpetaDetalleRepository carpetaDetalleRepository;
        @Mock
        private PersonaDetalleRepository personaDetalleRepository;
        @Mock
        private EventoService eventosService;
        @Mock
        private SolicitudesProrrogasService solicitudesProrrogasService;
 

        @Mock
        private ConfiguracionesRepository configuracionesRepository;

        private TipoJuicio tipoJuicio;
        private Juzgado juzgado;
        private TipoPartes actor;
        private TipoPartes demandado;
        private DocumentoSaveRecord recordRequest;
        private JuzgadoFolios juzgadoFolios;
        private Materia materia;

        @BeforeEach
        public void setUp() {
                materia = MateriaSetUp.createMateria();

                Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
                Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
                TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());

                tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);

                actor = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio);
                tipoPartesRepository.save(actor);
                demandado = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio).setNombre("Demandado");
                tipoPartesRepository.save(demandado);

                Sede sede = SedeSetUp.createSede();
                sede.setDistrito(distrito);
                sede.setDomicilio(domicilio);
                sede = sedeRepository.save(sede);

                juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
                juzgado.setContadorAsignaciones(0);
                juzgado.setMaxAsignacionesRonda(0);

                juzgadoFolios = createJuzgadoFolios();
                juzgadoFolios.setJuzgado(juzgado);

                juzgadoRepository.save(juzgado);
                recordRequest = DocumentoSetUp.createDocumentoSaveRecord(tipoJuicio.getId());

                ReflectionTestUtils.setField(documentoService, "correoDefensoria", "dircifame@htsjpuebla.gob.mx");
        }

        @Test
        void create_demanda() {
                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                demanda.getCarpeta().setFolio("1");
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

                Persona personaMock = mock(Persona.class);
                Oficialia oficialiaMock = mock(Oficialia.class);
                given(personaMock.getOficialia()).willReturn(oficialiaMock);
                given(personaService.getAuditor()).willReturn(personaMock);
                given(oficialiaMock.getId()).willReturn(1);

                Juzgado juzgadoMock = JuzgadoSetUp.createJuzgado();// mock(Juzgado.class);

                juzgadoMock.setTipoJuicios(List.of(tipoJuicio));

                List<Juzgado> juzgadosRelacionados = Arrays.asList(juzgadoMock);
                given(juzgadoRepository.findJuzgadoByOficialiaIdAndTipoJuicio(oficialiaMock.getId(),
                                tipoJuicio.getId())).willReturn(juzgadosRelacionados);
                given(juzgadoService.getConexidadJuzgado(any(), any(), any())).willReturn(juzgadoMock);

                TipoPartes actorParte = mock(TipoPartes.class);
                given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Actor"), any()))
                                .willReturn(Optional.of(actorParte));
                given(tipoJuicioRepository.findById(1)).willReturn(Optional.of(tipoJuicio));

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);

                given(documentoRepository.save(any())).willReturn(demanda);
                given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());
                given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta());

                DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(),
                                TipoCarpeta.DEMANDA);
                DocumentoRecord response = documentoService.createDemanda(recordRequest);

                assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());
        }

        @Test
        void update_status_success() {
                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                demanda.getCarpeta().setFolio("1");
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                given(documentoRepository.findById(any())).willReturn(Optional.of(demanda));
                given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta().setEstatus(EstadoCarpeta.SALIDA));

                DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(),
                                demanda.getCarpeta().getTipoCarpeta());

                DocumentoRecord response = documentoService.updateStatus(demanda.getId(), 1);
                assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());

        }

        @Test
        void update_status_not_found() {
                given(documentoRepository.findById(any())).willReturn(Optional.empty());

                NotFoundException assertThrows = assertThrows(
                                NotFoundException.class,
                                () -> {
                                        documentoService.updateStatus(1, 1);
                                });

                assertThat(assertThrows.getMessage()).contains("Documento no encontrado");
        }

        @Test
        void getAll_return_page() {
                Pageable pageable = PageRequest.of(0, 10);
                Persona persona = PersonaSetUp.createPersona();
                persona.setJuzgado(JuzgadoSetUp.createJuzgado());
                persona.setOficialia(null);
                given(personaService.getAuditor()).willReturn(persona);

                Carpeta carpeta = CarpetaSetUp.create();
                carpeta.setJuzgado(JuzgadoSetUp.createJuzgado());
                carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
                Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());

                // Movimiento mock
                Movimiento movimiento = new Movimiento();
                movimiento.setDocumento(documento);
                movimiento.setCarpeta(carpeta);
                movimiento.setEstado("ENTRADA");
                movimiento.setMotivo("Prueba");

                Page<Movimiento> movimientoPage = new PageImpl<>(List.of(movimiento), pageable, 1);
                given(movimientoService.getAllBandejaEntrada(any(Pageable.class), eq(1), any(), eq(""), any(), any(),
                                any(), any(), any() ))
                                .willReturn(movimientoPage);

                // Act
                Page<DocumentoGridRecord> result = documentoService.getAll(null, pageable, "Todas");

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
        }

        @Test
        void asignaJuzgado() {
                int invocaciones = 2;

                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                demanda.getCarpeta().setFolio("1");

                Documento apelacion = DocumentoSetUp.create(tipoJuicio);
                apelacion.getCarpeta().setTipoCarpeta(TipoCarpeta.APELACION);
                apelacion.getCarpeta().setFolio("2");

                Documento exhorto = DocumentoSetUp.create(tipoJuicio);
                exhorto.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);
                exhorto.getCarpeta().setFolio("3");

                Documento[] documentos = { demanda, apelacion, exhorto };
                List<Juzgado> juzgadosSeleccionados = new ArrayList<>();
                for (Documento documento : documentos) {
                        TipoCarpeta tipoCarpeta = documento.getCarpeta().getTipoCarpeta();

                        given(juzgadoService.getJuzgado(any(TipoJuicio.class), any(TipoCarpeta.class), eq(null)))
                                        .willReturn(juzgado);

                        juzgado = juzgadoService.getJuzgado(documento.getCarpeta().getTipoJuicio(), tipoCarpeta, null);
                        assertThat(juzgado).isNotNull();

                        for (int i = 0; i < invocaciones; i++) {
                                juzgadoService.actualizarCarga(juzgado, tipoCarpeta, juzgadosSeleccionados);
                        }

                        juzgadoService.revisarCargaJuzgados(documento.getCarpeta().getTipoJuicio().getMateria(),
                                        tipoCarpeta, juzgadosSeleccionados);
                }

                verify(juzgadoService, times(invocaciones)).actualizarCarga(juzgado, TipoCarpeta.DEMANDA,
                                juzgadosSeleccionados);
                verify(juzgadoService, times(1)).revisarCargaJuzgados(demanda.getCarpeta().getTipoJuicio().getMateria(),
                                TipoCarpeta.DEMANDA, juzgadosSeleccionados);

                verify(juzgadoService, times(invocaciones)).actualizarCarga(juzgado, TipoCarpeta.APELACION,
                                juzgadosSeleccionados);
                verify(juzgadoService, times(1)).revisarCargaJuzgados(
                                apelacion.getCarpeta().getTipoJuicio().getMateria(),
                                TipoCarpeta.APELACION, juzgadosSeleccionados);

                verify(juzgadoService, times(invocaciones)).actualizarCarga(juzgado, TipoCarpeta.EXHORTO,
                                juzgadosSeleccionados);
                verify(juzgadoService, times(1)).revisarCargaJuzgados(exhorto.getCarpeta().getTipoJuicio().getMateria(),
                                TipoCarpeta.EXHORTO, juzgadosSeleccionados);

        }

        @Test
        void edit_anexos() {
                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                demanda.getCarpeta().setFolio("1").setSelloEstatus(SelloEstatus.VALIDO);
                demanda.setData(new DocumentoData().setExhortoProcedencia("Otra procedencia"));

                List<String> nuevosAnexos = Arrays.asList("INE", "Acta de nacimiento");
                String motivoEdita = "Corrección";

                List<Anexo> anexosActuales = Arrays.asList(new Anexo().setNombre("Acta de nacimiento"),
                                new Anexo().setNombre("IFE"));

                given(documentoRepository.findById(demanda.getId())).willReturn(Optional.of(demanda));
                given(anexoRepository.findAllByDocumentoId(demanda.getId())).willReturn(anexosActuales);

                DocumentoRecord result = documentoService.editarAnexos(demanda.getId(), nuevosAnexos, motivoEdita,
                                "Procedencia 1");

                verify(documentoRepository).save(demanda);
                verify(anexoRepository).delete(anexosActuales.get(1));
                verify(anexoRepository, times(1)).save(any(Anexo.class));

                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo(demanda.getId());

                assertThat(demanda.getCarpeta().getSelloEstatus()).isEqualTo(SelloEstatus.NO_VALIDO);
        }

        @Test
        void getDemandaById_notFoundException() {
                Integer documentoId = 1;
                List<String> nuevosAnexos = Arrays.asList("Anexo1", "Anexo2");
                String motivoEdita = "Corrección";

                given(documentoRepository.findById(documentoId)).willReturn(Optional.empty());

                NotFoundException exception = assertThrows(NotFoundException.class,
                                () -> documentoService.editarAnexos(documentoId, nuevosAnexos, motivoEdita, ""));

                assertThat(exception.getMessage()).contains("Documento no encontrado");
        }

        @Test
        void getDemandaById_success() {
                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                demanda.getCarpeta().setFolio("1");

                PersonaDocumentoRecord actorRecord = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "",
                                "", "",
                                "", "Actor", 1, demanda.getCarpeta().getId());
                PersonaDocumentoRecord demandadoRecord = new PersonaDocumentoRecord("María", "López", "Martínez", null,
                                "fisica", "", "", "", "", "Demandado", 2, demanda.getCarpeta().getId());

                List<PersonaDocumentoRecord> personas = Arrays.asList(actorRecord, demandadoRecord);
                List<String> anexos = Arrays.asList("Acta de nacimiento", "INE");

                given(documentoRepository.findById(demanda.getId())).willReturn(Optional.of(demanda));
                given(personaDocumentoRepository.findPersonasByCarpetaId(demanda.getCarpeta().getId(), Rol.PRINCIPAL))
                                .willReturn(personas);
                given(anexoRepository.findNombresAnexosByDocumentoId(demanda.getId())).willReturn(anexos);

                DocumentoResponseRecord response = documentoService.getDemandaById(demanda.getId());

                PersonaDocumentoRecord actorResult = response.actor();
                PersonaDocumentoRecord demandadoResult = response.demandado();
                List<String> anexosResult = response.anexos();

                assertThat(response).isNotNull();
                assertThat(actorResult).isNotNull();
                assertThat(actorResult.nombre()).isEqualTo(actorRecord.nombre());
                assertThat(actorResult.apellidoPaterno()).isEqualTo(actorRecord.apellidoPaterno());
                assertThat(actorResult.apellidoMaterno()).isEqualTo(actorRecord.apellidoMaterno());
                assertThat(actorResult.pseudonimo()).isEqualTo(actorRecord.pseudonimo());
                assertThat(actorResult.tipoPersona()).isEqualTo(actorRecord.tipoPersona());
                assertThat(actorResult.tipoParte()).isEqualTo(actorRecord.tipoParte());

                assertThat(demandadoResult).isNotNull();
                assertThat(demandadoResult.nombre()).isEqualTo(demandadoRecord.nombre());
                assertThat(demandadoResult.apellidoPaterno()).isEqualTo(demandadoRecord.apellidoPaterno());
                assertThat(demandadoResult.apellidoMaterno()).isEqualTo(demandadoRecord.apellidoMaterno());
                assertThat(demandadoResult.pseudonimo()).isEqualTo(demandadoRecord.pseudonimo());
                assertThat(demandadoResult.tipoPersona()).isEqualTo(demandadoRecord.tipoPersona());
                assertThat(demandadoResult.tipoParte()).isEqualTo(demandadoRecord.tipoParte());

                assertThat(anexosResult).isNotNull().containsExactly(anexos.get(0), anexos.get(1));
        }

        @Test
        void generateNumExpediente_demanda() {
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                String numExpediente = documentoService.generateNumExpediente(
                                juzgado, TipoCarpeta.DEMANDA);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]");
        }

        @Test
        void generateNumExpediente_exhorto() {
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                String numExpediente = documentoService.generateNumExpediente(
                                juzgado, TipoCarpeta.EXHORTO);
                assertThat(numExpediente).containsPattern("E[0-9]{6}/202[0-9]");
        }

        @Test
        void generateNumExpediente_apelacion() {
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                String numExpediente = documentoService.generateNumExpediente(
                                juzgado, TipoCarpeta.APELACION);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]");
        }

        @Test
        void generateNumExpediente_despacho() {
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                String numExpediente = documentoService.generateNumExpediente(
                                juzgado, TipoCarpeta.DESPACHO);
                assertThat(numExpediente).containsPattern("D[0-9]{6}/202[0-9]");
        }

        @Test
        void generateNumExpediente_apelacion_municipal() {
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                String numExpediente = documentoService.generateNumExpediente(
                                juzgado, TipoCarpeta.APELACION_MUNICIPAL);
                assertThat(numExpediente).containsPattern("T[0-9]{6}/202[0-9]");
        }

        @Test
        void generateNumExpediente_amparo() {
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                String numExpediente = documentoService.generateNumExpediente(
                                juzgado, TipoCarpeta.AMPARO);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]");
        }

        @Test
        void getAllHistorial() {
                Documento documento1 = DocumentoSetUp.create(tipoJuicio);
                documento1.getCarpeta().setFolio("Folio1");
                documento1.getCarpeta().setExpediente("Expediente1");
                documento1.getCarpeta().setEstatus(EstadoCarpeta.CAPTURA);

                Juzgado juzgado1 = new Juzgado();
                documento1.getCarpeta().setJuzgado(juzgado1);
                documento1.getCarpeta().getJuzgado().setMateria(MateriaSetUp.createMateria());

                TipoCarpeta tipoCarpeta1 = TipoCarpeta.DEMANDA;
                documento1.getCarpeta().setTipoCarpeta(tipoCarpeta1);

                Movimiento movimiento1 = new Movimiento().setDocumento(documento1)
                                .setEstado(EstadoCarpeta.CAPTURA.name());
                List<Movimiento> listPage = Arrays.asList(movimiento1);
                Page<Movimiento> pageDocumentos = new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                listPage.size());

                given(movimientoRepository.getAllBandejaHistorial(any(), any(), any(), any(Pageable.class)))
                                .willReturn(pageDocumentos);
                given(personaService.getAuditor()).willReturn(new Persona());

                Page<DocumentoGridRecord> result = documentoService.getAllHistorial(null, PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("folio", "Folio1")
                                .hasFieldOrPropertyWithValue("expediente", "Expediente1")
                                .hasFieldOrPropertyWithValue("estatus", EstadoCarpeta.CAPTURA);

                assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        void createPromocion() {
                Oficialia oficialia = new Oficialia();
                oficialia.setId(1);
                Persona auditor = new Persona();
                auditor.setId(1L);
                auditor.setOficialia(oficialia);
                Carpeta carpeta = CarpetaSetUp.create(tipoJuicio, juzgado);
                DocumentoData documentoData = new DocumentoData().setTipoPromocion(TipoPromocion.OFICIO);
                Documento promocion = DocumentoSetUp.create(tipoJuicio).setEstatus(EstadoCarpeta.CAPTURA);

                promocion.setData(documentoData);
                promocion.setFolio("");
                promocion.setTipoDocumento(TipoDocumento.PROMOCION);

                given(personaService.getAuditor()).willReturn(auditor);
                given(carpetaRepository.findById(any())).willReturn(Optional.of((carpeta)));
                given(documentoRepository.save(any())).willReturn(promocion);
                given(documentoRepository.getNextValPromocion()).willReturn(1L);
                given(documentoRepository.getNextValPromocion()).willReturn(1L);

                List<String> anexos = List.of("Anexo1", "Anexo2");
                DocumentoPromocionRecord documentoPromocionRecord = new DocumentoPromocionRecord(1,
                                TipoPromocion.OFICIO,
                                anexos, null);
                DocumentoPromocionResponseRecord documentoResponse = documentoService
                                .createPromocion(documentoPromocionRecord, null);

                assertThat(documentoResponse)
                                .isNotNull()
                                .hasFieldOrPropertyWithValue("id", promocion.getId())
                                .hasFieldOrPropertyWithValue("folio", promocion.getFolio())
                                .hasFieldOrPropertyWithValue("tipoDocumento", promocion.getTipoDocumento());
        }

        @Test
        void createPromocionEletronica() {
                Persona auditor = new Persona();
                auditor.setId(1L);
                Concepto concepto = new Concepto().setId(1).setDias(1).setEstado(Estado.ACTIVE).setNombre("Adjuntar");
                Carpeta carpeta = CarpetaSetUp.create(tipoJuicio, juzgado);
                DocumentoData documentoData = new DocumentoData().setTipoPromocion(TipoPromocion.CORREO_ELECTRONICO);
                Documento promocion = DocumentoSetUp.create(tipoJuicio);
                MockMultipartFile multipartFile = new MockMultipartFile(
                                "file",
                                "archivo.txt",
                                "text/plain",
                                "Contenido del archivo".getBytes(StandardCharsets.UTF_8));

                promocion.setData(documentoData);
                promocion.setFolio("");
                promocion.setTipoDocumento(TipoDocumento.PROMOCION);

                given(personaService.getAuditor()).willReturn(auditor);
                given(carpetaRepository.findById(any())).willReturn(Optional.of((carpeta)));
                given(documentoRepository.save(any())).willReturn(promocion);
                given(documentoRepository.getNextValPromocion()).willReturn(1L);
                given(documentoRepository.getNextValPromocion()).willReturn(1L);
                given(conceptoRepository.findByNombre(any())).willReturn(Optional.of(concepto));

                List<String> anexos = List.of("Anexo1", "Anexo2");
                DocumentoPromocionRecord documentoPromocionRecord = new DocumentoPromocionRecord(1,
                                TipoPromocion.CORREO_ELECTRONICO,
                                anexos, null);
                DocumentoPromocionResponseRecord documentoResponse = documentoService
                                .createPromocion(documentoPromocionRecord, multipartFile);

                assertThat(documentoResponse)
                                .isNotNull()
                                .hasFieldOrPropertyWithValue("id", promocion.getId())
                                .hasFieldOrPropertyWithValue("folio", promocion.getFolio())
                                .hasFieldOrPropertyWithValue("tipoDocumento", promocion.getTipoDocumento());
        }

        @Test
        void create_exhorto() {

                Oficialia oficialia = new Oficialia();
                oficialia.setId(1);

                Persona auditor = new Persona();
                auditor.setId(1L);
                auditor.setOficialia(oficialia);

                DocumentoExhortoRecord recordItem = new DocumentoExhortoRecord("", "", Arrays.asList("1", "2"));
                Documento exhorto = DocumentoSetUp.create(tipoJuicio);
                exhorto.getCarpeta().setFolio("1");
                exhorto.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);

                List<Juzgado> juzgadosRelacionadosExhorto = Arrays.asList(new Juzgado());

                given(personaService.getAuditor()).willReturn(auditor);
                given(juzgadoRepository.findJuzgadoExhortoByOficialiaId(oficialia.getId()))
                                .willReturn(juzgadosRelacionadosExhorto);

                given(tipoJuicioRepository.findByNombreIgnoreCase(any())).willReturn(Optional.of(tipoJuicio));
                given(juzgadoService.getJuzgado(any(TipoJuicio.class), any(TipoCarpeta.class), any()))
                                .willReturn(juzgado);

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                given(documentoRepository.save(any())).willReturn(exhorto);
                given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());
                given(carpetaRepository.save(any())).willReturn(exhorto.getCarpeta());

                DocumentoRecord documentoRecord = new DocumentoRecord(exhorto.getId(), exhorto.getCarpeta().getFolio(),
                                TipoCarpeta.EXHORTO);

                DocumentoRecord response = documentoService.createExhorto(recordItem);
                assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());
        }

        @Test
        void create_apelacion() {
                Oficialia oficialia = new Oficialia();
                oficialia.setId(1);

                Persona auditor = new Persona();
                auditor.setId(1L);
                auditor.setOficialia(oficialia);
                auditor.setJuzgado(new Juzgado().setId(1));

                TipoJuicio tipoJuicioItem = TipoJuicioSetUp.createTipoJuicio();
                Documento demanda = DocumentoSetUp.create(tipoJuicioItem);
                Carpeta carpetaMock = CarpetaSetUp.create();
                demanda.setCarpeta(carpetaMock);
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.APELACION);
                TipoPartes tipoPartesMock = new TipoPartes();

                given(personaService.getAuditor()).willReturn(auditor);
                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
                lenient().when(carpetaRepository.findById(carpetaMock.getId())).thenReturn(Optional.of(carpetaMock));
                lenient().when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(tipoJuicio));
                lenient().when(juzgadoService.getConexidadJuzgado(any(), any(), any())).thenReturn(new Juzgado());
                lenient().when(carpetaRepository.findById(carpetaMock.getId())).thenReturn(Optional.of(carpetaMock));
                lenient().when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(tipoJuicio));
                given(tipoPartesRepository.findById(any())).willReturn(Optional.of(tipoPartesMock));
                given(anexoRepository.save(any())).willReturn(new Anexo());
                given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta());
                given(documentoFoliosService.getFolio(any(), any(), any())).willReturn(1);
                given(documentoRepository.save(any(Documento.class))).willReturn(demanda);

                ApelacionRecord apelacionRecord = CarpetaSetUp.apelacionRecord();

                DocumentoRecord response = documentoService.createApelacion(apelacionRecord);
                assertThat(response)
                                .isNotNull()
                                .hasFieldOrPropertyWithValue("id", demanda.getId())
                                .hasFieldOrPropertyWithValue("folio", demanda.getCarpeta().getFolio())
                                .hasFieldOrPropertyWithValue("tipoCarpeta", TipoCarpeta.APELACION);
                verify(carpetaRepository).findById(apelacionRecord.carpetaId());
                verify(tipoJuicioRepository).findById(demanda.getCarpeta().getTipoJuicio().getId());
                verify(carpetaRepository).save(any(Carpeta.class));
                verify(documentoRepository).save(any(Documento.class));
                verify(anexoRepository).save(any(Anexo.class));
                verify(personaDocumentoRepository, times(apelacionRecord.apelacionPersonaRecords().size()))
                                .save(any(PersonaDocumento.class));
        }

        @Test
        void getAll_bandeja_salida_success() {
                DocumentoSalidaRecord documentoRecord = new DocumentoSalidaRecord(
                                1,
                                1,
                                "1",
                                "000001/2024",
                                1,
                                "Juzgado Primero",
                                "LABORAL",
                                TipoCarpeta.DEMANDA,
                                null,
                                LocalDateTime.now(),
                                SelloEstatus.VALIDO,
                                EstadoCarpeta.TURNADO);
                List<DocumentoSalidaRecord> listPage = Collections.singletonList(documentoRecord);

                given(documentoRepository.findByEstatusSalida(any(), any(), any(), any(), any(), any(), any()))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));
                given(personaService.getAuditor())
                                .willReturn(new Persona().setId(1L).setJuzgado(juzgado));

                Page<DocumentoSalidaResponseRecord> page = documentoService.getAllBandejaSalida("",
                                PageRequest.of(1, listPage.size()));
                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("movid", documentoRecord.movid())
                                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                                .hasFieldOrPropertyWithValue("expediente", documentoRecord.expediente())
                                .hasFieldOrPropertyWithValue("materia", documentoRecord.materia());
        }

        @Test
        void getAllBandejaRecepcion_return_page() {
                Documento demanda = DocumentoSetUp.create(tipoJuicio);

                demanda.getCarpeta().setFolio("1");
                demanda.getCarpeta().setJuzgado(juzgado);
                Concepto concepto = new Concepto().setId(1).setDias(1).setEstado(Estado.ACTIVE)
                                .setNombre("Distribución");

                demanda.getCarpeta().setConcepto(concepto);
                Movimiento movimiento = new Movimiento().setCarpeta(demanda.getCarpeta()).setMotivo("RECEPCION");
                List<Movimiento> listPage = Collections.singletonList(movimiento);
                Persona persona = new Persona().setJuzgado(juzgado).setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");
                Map<String, Object> origen = Map.of(
                                "centroTrabajo", "prueba",
                                "nombrePersona", "Juan Pérez");

                given(documentoService.getDocumentoForRenderOficialMayor(movimiento, movimiento.getCarpeta()))
                                .willReturn(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio()));

                given(movimientoService.getOrigen(any(), any())).willReturn(origen);

                given(personaService.getAuditor()).willReturn(persona);
                given(roleService.hasRole(any(String.class), any(String.class))).willReturn(true);
               
                //given(documentoRepository.findByCarpetaIdAndTipoDocumento(any(), any())).willReturn(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio()));

                List<EstadoCarpeta> list = Arrays.asList(EstadoCarpeta.TURNADO, EstadoCarpeta.RECEPCION);
                List<String> motivos = Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.RECEPCION.name());
                given(movimientoService.getAllBandejaRecepcion(
                                PageRequest.of(0, listPage.size()),
                                juzgado.getId(), list, "", motivos, persona, null, null, null, null, null))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));

                Page<DocumentoBandejaRecepcionRecord> page = documentoService.getAllBandejaRecepcion("",
                                PageRequest.of(0, listPage.size()), "" );

                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("carpetaId", demanda.getId())
                                .hasFieldOrPropertyWithValue("folio", demanda.getCarpeta().getFolio())
                                .hasFieldOrPropertyWithValue("tipoEntrada", "Demanda")
                                .hasFieldOrPropertyWithValue("expediente", demanda.getCarpeta().getExpediente());
        }

        @Test
        void getOrigen_juzgado() {
                // Mismo juzgado
                Movimiento movimiento = new Movimiento().setJuzgado(juzgado);
                Persona persona = new Persona().setJuzgado(juzgado);
                Map<String, Object> origenPrincipal = Map.of(
                                "centroTrabajo", "prueba",
                                "nombrePersona", persona.getJuzgado().getNombre());
                                
                given(movimientoService.getOrigen(any(), any())).willReturn(origenPrincipal);
                Map<String, Object> origen = documentoService.getOrigen(movimiento, persona);
                assertThat(origen.get("name").toString()).contains(persona.getJuzgado().getNombre());
                assertThat(origen.get("isInterno").toString().toLowerCase()).contains("false");
        }

        @Test
        void getOrigen_oficialia() {
                // Misma oficialia
                Oficialia oficialia = new Oficialia().setNombre("Oficialia 1").setId(2);
                Movimiento movimiento = new Movimiento().setOficialia(oficialia);
                Persona persona = new Persona().setOficialia(oficialia).setNombre("Juan");
                 Map<String, Object> origenPrincipal = Map.of(
                                "centroTrabajo", "prueba",
                                "nombrePersona", oficialia.getNombre());

                given(movimientoService.getOrigen(any(), any())).willReturn(origenPrincipal);
                Map<String, Object> origen = documentoService.getOrigen(movimiento, persona);
                assertThat(origen.get("name").toString()).contains(oficialia.getNombre());
                assertThat(origen.get("isInterno").toString().toLowerCase()).contains("false");
        }

        @Test
        void getOrigen_invalid() {
                Movimiento movimiento = new Movimiento();
                Persona persona = new Persona();
                 Map<String, Object> origenPrincipal= Map.of(
                                "centroTrabajo", "prueba",
                                "nombrePersona", "");

                given(movimientoService.getOrigen(any(), any())).willReturn(origenPrincipal);
                Map<String, Object> origen = documentoService.getOrigen(movimiento, persona);
                assertThat(origen.get("name").toString()).contains("");
                assertThat(origen.get("isInterno").toString().toLowerCase()).contains("false");
        }

        @Test
        void testProcesarTipoCarpeta() {

                Object[] resultDemanda = documentoService.procesarTipoCarpeta("D.1");
                assertAll(
                                () -> assertEquals(TipoCarpeta.DEMANDA, resultDemanda[0],
                                                "TipoCarpeta debe ser DEMANDA"),
                                () -> assertNull(resultDemanda[1], "TipoDocumento debe ser null"),
                                () -> assertEquals(1, resultDemanda[2], "Folio debe ser 1"));

                Object[] resultPromocion = documentoService.procesarTipoCarpeta("P.5");
                assertAll(
                                () -> assertNull(resultPromocion[0], "TipoCarpeta debe ser null"),
                                () -> assertEquals(TipoDocumento.PROMOCION, resultPromocion[1],
                                                "TipoDocumento debe ser PROMOCION"),
                                () -> assertEquals(5, resultPromocion[2], "Folio debe ser 5"));

                Object[] resultExhorto = documentoService.procesarTipoCarpeta("E.100");
                assertAll(
                                () -> assertEquals(TipoCarpeta.EXHORTO, resultExhorto[0],
                                                "TipoCarpeta debe ser EXHORTO"),
                                () -> assertNull(resultExhorto[1], "TipoDocumento debe ser null"),
                                () -> assertEquals(100, resultExhorto[2], "Folio debe ser 100"));

                Object[] resultApelacion = documentoService.procesarTipoCarpeta("A.12");
                assertAll(
                                () -> assertEquals(TipoCarpeta.APELACION, resultApelacion[0],
                                                "TipoCarpeta debe ser APELACION"),
                                () -> assertNull(resultApelacion[1], "TipoDocumento debe ser null"),
                                () -> assertEquals(12, resultApelacion[2], "Folio debe ser 12"));

                String keyInvalida = "X.9";
                IllegalArgumentException exceptionTipoDesconocido = assertThrows(IllegalArgumentException.class, () -> {
                        documentoService.procesarTipoCarpeta(keyInvalida);
                });
                assertEquals("El tipo de carpeta es desconocido", exceptionTipoDesconocido.getMessage(),
                                "Carpeta es desconocida");

        }

        @Test
        void procersar_getQR() {

                Object[] apelacion = documentoService.getQR("A.1");
                assertAll(
                                () -> assertEquals("A", apelacion[0], "El prefijo no es correcto"),
                                () -> assertEquals(1, apelacion[1], "El folio no es correcto"));

                Object[] exhorto = documentoService.getQR("E.9");
                assertAll(
                                () -> assertEquals("E", exhorto[0], "El prefijo no es correcto"),
                                () -> assertEquals(9, exhorto[1], "El folio no es correcto"));

                Object[] promocion = documentoService.getQR("P.1");
                assertAll(
                                () -> assertEquals("P", promocion[0], "El prefijo no es correcto"),
                                () -> assertEquals(1, promocion[1], "El folio no es correcto"));

                String invalidQR = "D123";
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        documentoService.getQR(invalidQR);
                });
                assertEquals("El código QR tiene un formato inválido.", exception.getMessage(),
                                "Código QR con formato inválido");
                String invalidQR2 = "D.abc";
                assertThrows(NumberFormatException.class, () -> documentoService.getQR(invalidQR2),
                                "Folio no numérico");

                String emptyQR = "";
                assertThrows(IllegalArgumentException.class, () -> documentoService.getQR(emptyQR), "Cadena vacía");

        }

        @Test
        void send_to_bandeja_recepcion_success() {
                Concepto concepto = new Concepto().setId(1).setDias(1).setEstado(Estado.ACTIVE)
                                .setNombre("Distribución");
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setConcepto(concepto);
                documento.getCarpeta().setFolio("1");
                documento.getCarpeta().setJuzgado(juzgado);
                documento.setTipoDocumento(TipoDocumento.PROMOCION);
                Movimiento movimiento1 = new Movimiento().setDocumento(documento).setMotivo("SALIDA");
                Carpeta carpeta = CarpetaSetUp.create(tipoJuicio, juzgado);
                carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);
                Movimiento movimiento2 = new Movimiento().setCarpeta(carpeta).setMotivo("SALIDA");
                List<Movimiento> movimientoList = Arrays.asList(movimiento1, movimiento2);
                Persona persona = PersonaSetUp.createPersona().setJuzgado(juzgado)
                                .setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");

                given(movimientoRepository.findAllById(anyList())).willReturn(movimientoList);
                given(personaRepository.findById(any(Long.class))).willReturn(Optional.of(persona));
                given(personaService.getAuditor()).willReturn(persona);
                given(documentoRepository.save(any(Documento.class))).willReturn(documento);
                given(carpetaRepository.save(any(Carpeta.class))).willReturn(carpeta);
                given(conceptoRepository.findByNombre(any())).willReturn(Optional.of(concepto));

                List<Integer> idList = Arrays.asList(movimiento1.getId(), movimiento2.getId());
                Integer personaCarrito = Math.toIntExact(persona.getId());

                documentoService.sendToBandejaRecepcion(idList, personaCarrito);
                verify(movimientoRepository).findAllById(idList);
                verify(personaRepository).findById(Long.valueOf(personaCarrito));
                verify(personaService).getAuditor();
                verify(documentoRepository, times(1)).save(documento);
                verify(carpetaRepository).save(carpeta);
                verify(movimientoRepository, times(2)).save(any(Movimiento.class));
        }

        @Test
        void send_to_bandeja_recepcion_persona_not_found() {
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.getCarpeta().setFolio("1");
                documento.getCarpeta().setJuzgado(juzgado);
                Movimiento movimiento1 = new Movimiento().setDocumento(documento).setMotivo("SALIDA");
                Carpeta carpeta = CarpetaSetUp.create(tipoJuicio, juzgado);
                Movimiento movimiento2 = new Movimiento().setCarpeta(carpeta).setMotivo("SALIDA");
                List<Movimiento> movimientoList = Arrays.asList(movimiento1, movimiento2);
                Persona persona = PersonaSetUp.createPersona().setJuzgado(juzgado)
                                .setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");

                given(movimientoRepository.findAllById(anyList())).willReturn(movimientoList);

                List<Integer> idList = Arrays.asList(movimiento1.getId(), movimiento2.getId());
                Integer personaCarrito = Math.toIntExact(persona.getId());

                NotFoundException assertThrows = assertThrows(
                                NotFoundException.class,
                                () -> {
                                        documentoService.sendToBandejaRecepcion(idList, personaCarrito);
                                });

                assertThat(assertThrows.getMessage()).contains("Persona no encontrada");
        }

        @Test
        void getIndicadores_success() {
                Persona persona = new Persona().setJuzgado(juzgado).setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");
                Documento demanda = DocumentoSetUp.create(tipoJuicio).setTipoDocumento(TipoDocumento.PROMOCION);
                demanda.getCarpeta().setFolio("1");
                demanda.getCarpeta().setJuzgado(juzgado);
                Concepto concepto = new Concepto().setId(1).setDias(1).setEstado(Estado.ACTIVE)
                                .setNombre("Distribución");
                demanda.getCarpeta().setConcepto(concepto);
                Movimiento movimiento = new Movimiento()
                                .setCarpeta(demanda.getCarpeta())
                                .setMotivo("RECEPCION")
                                .setFechaAsignacion(LocalDateTime.now())
                                .setPersona(persona)
                                .setDocumento(demanda);
                List<Movimiento> listPage = Collections.singletonList(movimiento);
                Page<Movimiento> page = new PageImpl<>(listPage);

                given(etiquetaService.renderEtiquetaRecepcion(any(), any(Carpeta.class))).willReturn("Promocion");
                given(personaService.getAuditor()).willReturn(persona);
                given(movimientoService.getBandejaRecepcion(any(), any(), any(), any(), any(), any(), any(), any(),
                                any(), any(), any())).willReturn(page);

                IndicadoresRecord expected = new IndicadoresRecord(1, 1, 0, 0);

                IndicadoresRecord result = documentoService.getIndicadores();

                assertThat(result).isEqualTo(expected);
        }

        @Test
        void crearOficioAdministrativoTest() {
                Integer institucionId = 1;
                LocalDate fechaEmision = LocalDate.now();
                String asunto = "Prueba asunto";
                Integer carpetaId = 1;
                Persona persona = PersonaSetUp.createPersona().setJuzgado(JuzgadoSetUp.createJuzgado());

                Institucion institucion = InstitucionSetUp.createInstitucion(Estado.ACTIVE);
                Integer folio = 1;

                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setFolio("1")
                                .setInstitucion(new Institucion().setNombre("institucion1"))
                                .setEstatus(EstadoCarpeta.CREADO)
                                .setData(new DocumentoData().setFechaEmision(LocalDate.now()));

                given(personaService.getAuditor())
                                .willReturn(persona);
                given(documentoFoliosService.getFolio(TipoDocumento.OFICIO, persona.getJuzgado(), null))
                                .willReturn(1);
                given(institucionRepository.findById(institucionId))
                                .willReturn(Optional.of(institucion));
                given(carpetaRepository.findById(carpetaId))
                                .willReturn(Optional.of(CarpetaSetUp.create()));
                given(documentoRepository.save(any(Documento.class)))
                                .willReturn(documento);

                Integer resultado = documentoService.createOficio(institucionId, fechaEmision, asunto, carpetaId);

                assertThat(resultado).isEqualTo(folio);
        }

        @Test
        void crearOficioJurisdiccionalTest() {
                Integer institucionId = 1;
                LocalDate fechaEmision = LocalDate.now();
                String asunto = "Prueba asunto";
                Integer carpetaId = null;
                Persona persona = PersonaSetUp.createPersona().setJuzgado(JuzgadoSetUp.createJuzgado());

                Institucion institucion = InstitucionSetUp.createInstitucion(Estado.ACTIVE);
                Integer folio = 1;

                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setFolio("1")
                                .setInstitucion(new Institucion().setNombre("institucion1"))
                                .setEstatus(EstadoCarpeta.CREADO)
                                .setData(new DocumentoData().setFechaEmision(LocalDate.now()));

                given(personaService.getAuditor())
                                .willReturn(persona);
                given(documentoFoliosService.getFolio(TipoDocumento.OFICIO, persona.getJuzgado(), null))
                                .willReturn(1);
                given(documentoRepository.save(any(Documento.class)))
                                .willReturn(documento);
                given(institucionRepository.findById(institucionId))
                                .willReturn(Optional.of(institucion));

                Integer resultado = documentoService.createOficio(institucionId, fechaEmision, asunto, carpetaId);

                assertThat(resultado).isEqualTo(folio);
        }

        @Test
        void getAllBandejaAsignados() {
                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                demanda.getCarpeta().setFolio("1");
                demanda.setData(new DocumentoData().setTipoPromocion(TipoPromocion.CORREO_ELECTRONICO));
                demanda.setTipoDocumento(TipoDocumento.PROMOCION);
                demanda.setEstatus(EstadoCarpeta.ASIGNADO);
                demanda.getCarpeta().setJuzgado(juzgado);
                demanda.setMigrado(Migrado.NO);
                Concepto concepto = new Concepto().setId(1).setDias(1).setEstado(Estado.ACTIVE)
                                .setNombre("Distribución");
                demanda.setConcepto(concepto);
                Movimiento movimiento = new Movimiento().setDocumento(demanda).setMotivo("ASIGNADO")
                                .setFechaAsignacion(LocalDateTime.now()).setEstado(EstadoCarpeta.ASIGNADO.name());
                List<Movimiento> listPage = Collections.singletonList(movimiento);

                given(documentoRepository.findByPersonaAsignada(anyString(), any(), any(), anyBoolean(), any(), any(),
                                any(), any(), any(), any()))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));
                given(personaService.getAuditor())
                                .willReturn(new Persona().setId(1L).setJuzgado(juzgado));
                Page<DocumentoAsignadoResponseRecord> page = documentoService.getAllAsignado("",null,
                                PageRequest.of(1, listPage.size()), null );
                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("id", demanda.getId())
                                .hasFieldOrPropertyWithValue("expediente", demanda.getCarpeta().getExpediente())
                                .hasFieldOrPropertyWithValue("tipoEntrada",
                                                StringUtils.capitalize(demanda.getTipoDocumento().name().toLowerCase()))
                                .hasFieldOrPropertyWithValue("concepto", concepto.getNombre())
                                .hasFieldOrPropertyWithValue("fechaTurnado", movimiento.getFechaAsignacion())
                                .hasFieldOrPropertyWithValue("fechaTermino",
                                                movimiento.getFechaAsignacion().plusDays(concepto.getDias()));
        }

        @Test
        void getDataDocumentoRecepcion() {
                Integer documentoId = 1;
                List<AnexoRecepcionRecord> anexos = new ArrayList<>();

                // Agregar instancias de AnexoRecepcionRecord a la lista
                anexos.add(new AnexoRecepcionRecord(1, EstadoAnexo.ASIGNADO, "Anexo 1"));
                anexos.add(new AnexoRecepcionRecord(2, EstadoAnexo.ASIGNADO, "Anexo 2"));
                anexos.add(new AnexoRecepcionRecord(3, EstadoAnexo.ASIGNADO, "Anexo 3"));
                given(documentoRepository.findById(documentoId))
                                .willReturn(Optional.of(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio())
                                                .setTipoDocumento(TipoDocumento.PROMOCION).setFolio("1")));

                given(anexoRepository.findAnexosByDocumentoId(documentoId))
                                .willReturn(anexos);

                DocumentoRecepcionRecord doc = documentoService.getDataDocumentoRecepcion(documentoId);

                assertNotNull(doc, "El DocumentoRecepcionRecord no debe ser nulo");
                assertEquals("1", doc.folio(), "El folio del documento no es el esperado");
                assertEquals("000001/2024", doc.expediente(), "El expediente del documento no es el esperado");
                assertEquals(StringUtils.capitalize(TipoDocumento.PROMOCION.name().toLowerCase()), doc.tipoEntrada(),
                                "El tipo de documento no es el esperado");
        }

        @Test
        void getAll_bandeja_oficios_success_without_detalle_contenido() {
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setFolio("1")
                                .setInstitucion(new Institucion().setNombre("institucion1"))
                                .setEstatus(EstadoCarpeta.CREADO)
                                .setData(new DocumentoData().setFechaEmision(LocalDate.now()));

                OficioResponseRecord oficioResponseRecord = new OficioResponseRecord(
                                1,
                                "1",
                                "institucion 1",
                                "Asunto 1",
                                EstadoCarpeta.CREADO,
                                EstadoCarpeta.CREADO.getEtiqueta(),
                                LocalDate.now(),
                                LocalDate.now(),
                                false,
                                false,
                                'o',
                                "000001/2025");

                List<OficioResponseRecord> listPage = Collections.singletonList(oficioResponseRecord);

                given(documentoRepository.findAllByTipoDocumento(any(String.class), any(TipoDocumento.class),
                                any(Pageable.class)))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));

                Page<OficioResponseRecord> page = documentoService.getAllOficios("",
                                PageRequest.of(1, listPage.size()));
                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("folio", oficioResponseRecord.folio())
                                .hasFieldOrPropertyWithValue("dependencia", oficioResponseRecord.dependencia())
                                .hasFieldOrPropertyWithValue("estatus", oficioResponseRecord.estatus())
                                .hasFieldOrPropertyWithValue("fechaEmision", oficioResponseRecord.fechaEmision())
                                .hasFieldOrPropertyWithValue("bandAcuse", oficioResponseRecord.bandAcuse())
                                .hasFieldOrPropertyWithValue("bandDigitalizado",
                                                oficioResponseRecord.bandDigitalizado());
        }

        @Test
        void getAll_bandeja_oficios_success_with_detalle_contenido() {
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setFolio("1")
                                .setInstitucion(new Institucion().setNombre("institucion1"))
                                .setEstatus(EstadoCarpeta.CREADO)
                                .setData(new DocumentoData().setFechaEmision(LocalDate.now()));

                OficioResponseRecord oficioResponseRecord = new OficioResponseRecord(
                                1,
                                "1",
                                "institucion 1",
                                "Asunto 1",
                                EstadoCarpeta.CREADO,
                                EstadoCarpeta.CREADO.getEtiqueta(),
                                LocalDate.now(),
                                LocalDate.now(),
                                true,
                                true,
                                'o',
                                "000001/2025");

                List<OficioResponseRecord> listPage = Collections.singletonList(oficioResponseRecord);

                given(documentoRepository.findAllByTipoDocumento(any(String.class), any(TipoDocumento.class),
                                any(Pageable.class)))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));

                Page<OficioResponseRecord> page = documentoService.getAllOficios("",
                                PageRequest.of(1, listPage.size()));
                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("folio", oficioResponseRecord.folio())
                                .hasFieldOrPropertyWithValue("dependencia", oficioResponseRecord.dependencia())
                                .hasFieldOrPropertyWithValue("estatus", oficioResponseRecord.estatus())
                                .hasFieldOrPropertyWithValue("fechaEmision", oficioResponseRecord.fechaEmision())
                                .hasFieldOrPropertyWithValue("bandAcuse", oficioResponseRecord.bandAcuse())
                                .hasFieldOrPropertyWithValue("bandDigitalizado",
                                                oficioResponseRecord.bandDigitalizado());
        }

        @Test
        void cancel_oficio_success() {
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setFolio("1")
                                .setInstitucion(new Institucion().setNombre("institucion1"))
                                .setEstatus(EstadoCarpeta.CREADO)
                                .setData(new DocumentoData().setFechaEmision(LocalDate.now()));
                Persona persona = PersonaSetUp.createPersona().setJuzgado(juzgado)
                                .setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");
                Movimiento movimiento = new Movimiento().setDocumento(documento).setMotivo("CREADO").setId(1);

                given(documentoRepository.findById(any(Integer.class)))
                                .willReturn(Optional.of(documento));
                given(personaService.getAuditor()).willReturn(persona);
                given(movimientoService.createMovimento(any(), any(), any(), any(), any())).willReturn(movimiento);

                documentoService.cancelOficio(1);
                verify(personaService).getAuditor();
                verify(documentoRepository).findById(1);
                verify(documentoRepository).actualizarEstatus(1, EstadoCarpeta.CANCELADO);
                verify(movimientoService).createMovimento(any(), any(), any(), any(), any());
        }

        @Test
        void movimientoPersonalJuzgado_success() {
                PersonalJuzgadoRecord personalJuzgadoRecord = new PersonalJuzgadoRecord(51, 2, "DEMANDA", 3);
                Concepto concepto = ConceptoSetUp.createConcepto();
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                Carpeta carpeta = CarpetaSetUp.create();
                Persona persona = PersonaSetUp.createPersona()
                                .setJuzgado(juzgado);
                Movimiento movimiento = new Movimiento()
                                .setCarpeta(carpeta)
                                .setDocumento(null)
                                .setFechaAsignacion(LocalDateTime.now())
                                .setMotivo("ASIGNADO")
                                .setPersona(persona)
                                .setOficialia(null)
                                .setJuzgado(juzgado);
                String duration = (documento.getCarpeta().getHoras() != null && documento.getCarpeta().getHoras() > 0)
                                ? documento.getCarpeta().getHoras() + "h"
                                : concepto.getDias().toString() + "d";

                when(conceptoRepository.findById(personalJuzgadoRecord.idConcepto())).thenReturn(Optional.of(concepto));
                when(carpetaRepository.findById(any(Integer.class))).thenReturn(Optional.of(carpeta));
                when(personaService.getAuditor()).thenReturn(persona);
                when(movimientoService.createMovimentoTurnado(carpeta, null, persona, null,
                                EstadoCarpeta.ASIGNADO.name(), concepto.getNombre(), null, duration))
                                .thenReturn(movimiento);

                MovimientoPersonalJuzgadoRecord resultado = documentoService
                                .movimientoPersonalJuzgado(personalJuzgadoRecord);

                assertNotNull(resultado);
                assertEquals(personalJuzgadoRecord.idDocumentoRecepcion(), resultado.carpeta());
                assertEquals(movimiento.getFechaAsignacion(), resultado.fechaAsignacion());
                assertEquals(persona.getNombre(), resultado.persona());
                assertEquals(movimiento.getMotivo(), resultado.movimiento());
                assertEquals(persona.getJuzgado().getNombre(), resultado.juzgado());
        }

        @Test
        void turnadoPersonalJuzgado_success() {
                AsignadoTurnadoRecord record1 = new AsignadoTurnadoRecord(51, 150, 1, 7, Prioridad.NORMAL);
                AsignadoTurnadoRecord record2 = new AsignadoTurnadoRecord(51, 150, 1, 7, Prioridad.URGENTE);
                List<AsignadoTurnadoRecord> records = Arrays.asList(record1, record2);

                Concepto concepto = ConceptoSetUp.createConcepto();
                Carpeta carpeta = CarpetaSetUp.create();
                Persona persona = PersonaSetUp.createPersona()
                                .setJuzgado(juzgado);
                Movimiento movimiento = new Movimiento()
                                .setCarpeta(carpeta)
                                .setDocumento(null)
                                .setFechaAsignacion(LocalDateTime.now())
                                .setMotivo("ASIGNADO")
                                .setPersona(persona)
                                .setOficialia(null)
                                .setJuzgado(juzgado);

                when(personaRepository.findById(record1.idPersonalJuzgado().longValue()))
                                .thenReturn(Optional.of(persona));
                when(conceptoRepository.findById(record1.idConcepto())).thenReturn(Optional.of(concepto));
                when(carpetaRepository.findById(any(Integer.class))).thenReturn(Optional.of(carpeta));
                when(personaService.getAuditor()).thenReturn(persona);
                when(movimientoService.createMovimentoTurnado(any(), any(), any(), any(), any(), any(), any(), any()))
                                .thenReturn(movimiento);

                List<MovimientoPersonalJuzgadoRecord> resultados = documentoService.turnadoPersonalJuzgado(records);

                assertNotNull(resultados);
                assertEquals(2, resultados.size());
        }

        @Test
        void testSendEmailFamiliar() {
                TipoJuicioDemandasRecord tipoJuicioRecord = new TipoJuicioDemandasRecord(tipoJuicio.getId(),
                                tipoJuicio.getNombre());
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setData(new DocumentoData().setTiposJuicios(Arrays.asList(tipoJuicioRecord)));
                documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                documento.getCarpeta().setFolio("1");

                PersonaDocumentoItemRecord actorRecord = new PersonaDocumentoItemRecord(
                                "William", "Perez", "", null,
                                "fisica", 1, "DIAG021007HTLLCDA3", "", "", "2461140011", "juan@gmail.com");

                PersonaDocumentoItemRecord demandadoRecord = new PersonaDocumentoItemRecord(
                                "María", "López", "Martínez", null,
                                "fisica", 2, "DIAG021007HTLLCDA5", "", "", "2462240022", "mariaLopez@gmail.com");
                List<String> anexos = Arrays.asList("Acta de nacimiento", "INE");
                DocumentoData documentoData = new DocumentoData();

                DocumentoSaveRecord documentoSaveRecord = new DocumentoSaveRecord(
                                actorRecord,
                                demandadoRecord,
                                anexos,
                                tipoJuicio.getId(),
                                documentoData);
                Carpeta carpeta = new Carpeta()
                                .setId(1)
                                .setVersion(1)
                                .setFolio("1")
                                .setExpediente("000001/2024")
                                .setEstatus(EstadoCarpeta.CAPTURA)
                                .setTipoJuicio(tipoJuicio)
                                .setSelloEstatus(SelloEstatus.VALIDO);
                when(audienciaRepository.getSalaNombreByCarpetaId(any())).thenReturn("1");
                when(selloGenerator.updateExpedientePorTipoJuicio(any())).thenReturn("J/T/000001/2024/FT-FO");

                documentoService.sendEmailFamiliar(documentoSaveRecord, documento, carpeta, tipoJuicio);
                ArgumentCaptor<Map<String, Object>> sendEmailCaptor = ArgumentCaptor.forClass(Map.class);
                verify(emailService).sendMail(
                                eq(List.of("dircifame@htsjpuebla.gob.mx")),
                                eq(Collections.emptyList()),
                                eq(Collections.emptyList()),
                                eq("Recepción de Asignación de Juicio"),
                                eq("EmailDemandaFamiliar.ftl"),
                                sendEmailCaptor.capture());

                Map<String, Object> sendEmailData = sendEmailCaptor.getValue();

                assertEquals("Laboral", sendEmailData.get("juicio"));
                assertEquals("1", sendEmailData.get("sala"));
                assertEquals("J/T/000001/2024/FT-FO", sendEmailData.get("carpetaDigital"));
                assertEquals(tipoJuicio.getNombre(), sendEmailData.get("tipoJuicio"));
                assertEquals("William Perez".trim(), sendEmailData.get("actor").toString().trim());
                assertEquals("2461140011", sendEmailData.get("telefono"));
                assertEquals("juan@gmail.com", sendEmailData.get("correo"));
                assertEquals("María López Martínez".trim(), sendEmailData.get("demandado").toString().trim());
                assertEquals("<ul><li>Acta de nacimiento</li><li>INE</li></ul>", sendEmailData.get("anexos"));
        }

        @Test
        void testSendEmailFamiliarSinAnexos() {
                TipoJuicioDemandasRecord tipoJuicioRecord = new TipoJuicioDemandasRecord(tipoJuicio.getId(),
                                tipoJuicio.getNombre());
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setData(new DocumentoData().setTiposJuicios(Arrays.asList(tipoJuicioRecord)));
                documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                documento.getCarpeta().setFolio("1");

                PersonaDocumentoItemRecord actorRecord = new PersonaDocumentoItemRecord(
                                "William", "Perez", "", null,
                                "fisica", 1, "DIAG021007HTLLCDA3", "", "", "2461140011", "juan@gmail.com");

                PersonaDocumentoItemRecord demandadoRecord = new PersonaDocumentoItemRecord(
                                "María", "López", "", null,
                                "fisica", 2, "DIAG021007HTLLCDA5", "", "", "2462240022", "mariaLopez@gmail.com");
                List<String> anexos = null; // Anexos como null para probar el caso sin anexos
                DocumentoData documentoData = new DocumentoData();

                DocumentoSaveRecord documentoSaveRecord = new DocumentoSaveRecord(
                                actorRecord,
                                demandadoRecord,
                                anexos,
                                tipoJuicio.getId(),
                                documentoData);
                Carpeta carpeta = new Carpeta()
                                .setId(1)
                                .setVersion(1)
                                .setFolio("1")
                                .setExpediente("000001/2024")
                                .setEstatus(EstadoCarpeta.CAPTURA)
                                .setTipoJuicio(tipoJuicio)
                                .setSelloEstatus(SelloEstatus.VALIDO);
                when(audienciaRepository.getSalaNombreByCarpetaId(any())).thenReturn("1");
                when(selloGenerator.updateExpedientePorTipoJuicio(any())).thenReturn("J/T/000001/2024/FT-FO");

                documentoService.sendEmailFamiliar(documentoSaveRecord, documento, carpeta, tipoJuicio);
                ArgumentCaptor<Map<String, Object>> sendEmailCaptor = ArgumentCaptor.forClass(Map.class);
                verify(emailService).sendMail(
                                eq(List.of("dircifame@htsjpuebla.gob.mx")),
                                eq(Collections.emptyList()),
                                eq(Collections.emptyList()),
                                eq("Recepción de Asignación de Juicio"),
                                eq("EmailDemandaFamiliar.ftl"),
                                sendEmailCaptor.capture());

                Map<String, Object> sendEmailData = sendEmailCaptor.getValue();

                assertEquals("Laboral", sendEmailData.get("juicio"));
                assertEquals("1", sendEmailData.get("sala"));
                assertEquals("J/T/000001/2024/FT-FO", sendEmailData.get("carpetaDigital"));
                assertEquals(tipoJuicio.getNombre(), sendEmailData.get("tipoJuicio"));
                assertEquals("William Perez".trim(), sendEmailData.get("actor").toString().trim());
                assertEquals("2461140011", sendEmailData.get("telefono"));
                assertEquals("juan@gmail.com", sendEmailData.get("correo"));
                assertEquals("María López".trim(), sendEmailData.get("demandado").toString().trim());
                assertEquals("<ul><li>Sin anexo</li></ul>", sendEmailData.get("anexos"));
        }

        @Test
        void deleteAsignado() {
                Integer id = 1;
                String nombreParticipante = "Juan Perez";
                PersonaDocumento personaDocumento = new PersonaDocumento();
                personaDocumento.setId(id);
                personaDocumento.setNombre(nombreParticipante);
                Carpeta carpeta = CarpetaSetUp.create();
                personaDocumento.setCarpeta(carpeta);
                Persona auditor = new Persona();
                auditor.setNombre("Pedro");

                // Configuración de los mocks
                when(personaDocumentoRepository.findById(id)).thenReturn(Optional.of(personaDocumento));
                when(personaService.getAuditor()).thenReturn(auditor);
                Optional<PersonaDetalle> personaDetalle = Optional.empty(); // Suponemos que no hay detalle asociado
                when(personaDetalleRepository.findByPersonaDocumentoId(id)).thenReturn(personaDetalle);

                // Llamada al método a probar
                documentoService.deleteAsignado(id);

                // Verificación de los efectos secundarios
                verify(movimientoService).createMovimento(
                                eq(carpeta),
                                isNull(),
                                eq(auditor),
                                eq("ELIMINADO DE PARTICIPANTE " + nombreParticipante),
                                isNull());
                verify(personaDetalleRepository, never()).deleteById(any()); // Verifica que no se eliminó ningún
                                                                             // detalle
                verify(personaDocumentoRepository).deleteById(id);
                verify(personaDocumentoRepository).flush();
        }

        @Test
        void deleteAsignado_NotFound() {
                Integer id = 1;
                when(personaDocumentoRepository.findById(id)).thenReturn(Optional.empty());

                // Verifica que se lanza la excepción EntityNotFoundException cuando no se
                // encuentra el documento
                EntityNotFoundException exception = assertThrows(
                                EntityNotFoundException.class,
                                () -> documentoService.deleteAsignado(id));

                assertEquals("No se encontró la persona documento con ID: " + id, exception.getMessage());
                verify(personaDocumentoRepository, never()).deleteById(id);
                verify(personaDocumentoRepository, never()).flush();
        }

        @Test
        void deleteAsignado_ConstraintViolation() {
                Integer id = 1;
                String nombreParticipante = "Juan Perez";
                PersonaDocumento personaDocumento = new PersonaDocumento();
                personaDocumento.setId(id);
                personaDocumento.setNombre(nombreParticipante);
                Carpeta carpeta = CarpetaSetUp.create();
                personaDocumento.setCarpeta(carpeta);
                Persona auditor = new Persona();
                auditor.setNombre("Pedro");

                // Configuración de los mocks
                when(personaDocumentoRepository.findById(id)).thenReturn(Optional.of(personaDocumento));
                when(personaService.getAuditor()).thenReturn(auditor);
                Optional<PersonaDetalle> personaDetalle = Optional.empty(); // Suponemos que no hay detalle asociado
                when(personaDetalleRepository.findByPersonaDocumentoId(id)).thenReturn(personaDetalle);

                // Simular la excepción DataIntegrityViolationException al intentar eliminar
                doThrow(new DataIntegrityViolationException(
                                "No se puede eliminar debido a dependencias existentes con otros registros"))
                                .when(personaDocumentoRepository).deleteById(id);

                // Verifica que se lanza ConstraintViolationException
                ConstraintViolationException exception = assertThrows(
                                ConstraintViolationException.class,
                                () -> documentoService.deleteAsignado(id));

                assertTrue(exception.getMessage().contains(Messages.CONSTRAINT_ERROR));
                verify(personaDocumentoRepository).deleteById(id);
                verify(personaDocumentoRepository, never()).flush();
        }

        @Test
        void create_demandaAntiguas() {

                Documento demanda = DocumentoSetUp.create(tipoJuicio);
                Concepto concepto = new Concepto();
                demanda.getCarpeta().setFolio("1");
                demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

                TipoJuicio tipoJuicioOral = new TipoJuicio();
                tipoJuicioOral.setNombre("Oral");

                TipoJuicio tipoJuicioTradicional = new TipoJuicio();
                tipoJuicioTradicional.setNombre("Tradicional");

                tipoJuicioTradicional.setTipoSistema(new TipoSistema().setNombre("Tradicional"));
                List<TipoJuicio> listaTipoJuicios = new ArrayList<>();
                listaTipoJuicios.add(tipoJuicioTradicional);
                listaTipoJuicios.add(tipoJuicioOral);

                Persona persona = PersonaSetUp.createPersona();
                persona.getJuzgado().setTipoJuicios(listaTipoJuicios);

                given(personaService.getAuditor()).willReturn(persona);
                lenient().when(juzgadoService.getJuzgadoFolios(any(), any())).thenReturn(juzgadoFolios);
                lenient().when(juzgadoService.checkYearJuzgadoFolios(any())).thenReturn(juzgadoFolios);
                given(documentoRepository.save(any())).willReturn(demanda);
                given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Actor"), any()))
                                .willReturn(Optional.of(actor));
                given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Demandado"), any()))
                                .willReturn(Optional.of(demandado));
                given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());
                given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta());
                given(digitalizacionService.guardarArchivo(any(), any()))
                                .willReturn(new DigitalizacionRecord(demanda.getId(), "ruta/del/archivo",
                                                "archivo.pdf"));
                concepto.setId(1).setNombre("Distribución");
                given(conceptoRepository.findByNombre("Distribución")).willReturn(Optional.of(concepto));
                DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(),
                                TipoCarpeta.DEMANDA);
                MockMultipartFile multipartFile = new MockMultipartFile(
                                "file",
                                "archivo.txt",
                                "text/plain",
                                "Contenido del archivo".getBytes(StandardCharsets.UTF_8));

                PersonaDocumentoItemRecord actorItem = new PersonaDocumentoItemRecord(
                                "William", "Perez", "", null,
                                "fisica", 1, "DIAG021007HTLLCDA3", "", "", "2461140011", "juan@gmail.com");

                PersonaDocumentoItemRecord demandadoItem = new PersonaDocumentoItemRecord(
                                "María", "López", "", null,
                                "fisica", 2, "DIAG021007HTLLCDA5", "", "", "2462240022", "mariaLopez@gmail.com");

                List<String> anexos = Arrays.asList("Acta de nacimiento", "INE");

                DocumentoData documentoData = new DocumentoData();
                documentoData.setExhortoObservaciones("Observaciones");
                documentoData.setExhortoProcedencia("Procedencia");

                DocumentoAntiguoSaveRecord recordRt = new DocumentoAntiguoSaveRecord(actorItem, demandadoItem, anexos,
                                tipoJuicio.getId(), documentoData, "00111", "2024");
                DocumentoRecord response = documentoService.createDemandaAntigua(recordRt, multipartFile);
                assertThat(response)
                                .isOfAnyClassIn(DocumentoRecord.class)
                                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());

                verify(personaService).getAuditor();
                verify(carpetaRepository).save(any());
                verify(documentoRepository).save(any());
                verify(digitalizacionService).guardarArchivo(multipartFile, demanda.getId());
                verify(movimientoService).createMovimento(any(), any(), any(), any(), any());
        }

        @Test
        void createAmparo() {
                AmparoRecord amparoRecord = new AmparoRecord(1,
                                "AD",
                                LocalDate.now(),
                                null,
                                0,
                                CatalogoSentidoAmparo.CONCEDE.name(),
                                CatalogoImpugnacionAmparo.CONFIRMA.name(),
                                "JUAN PEREZ",
                                1,
                                null);
                String pieza = "000001/2024/AD01";
                Carpeta carpeta = CarpetaSetUp.create();
                Persona persona = PersonaSetUp.createPersona();
                Concepto concepto = ConceptoSetUp.createConcepto();
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.setId(10);
                documento.getCarpeta().setExpediente(pieza);

                AmparoRecordResponse response;

                given(personaService.getAuditor()).willReturn(persona);
                given(carpetaRepository.findById(any())).willReturn(Optional.of(carpeta));
                given(conceptoRepository.findByNombre(any())).willReturn(Optional.of(concepto));
                given(carpetaService.createPieza(any(), any())).willReturn(documento.getCarpeta());
                response = documentoService.createAmparo(amparoRecord);

                assertThat(response).isNotNull().hasFieldOrPropertyWithValue("numeroPieza", pieza);
        }

        @Test
        void getExhortoById_Success() {

                Documento documento = DocumentoSetUp.create(tipoJuicio);
                documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
                documento.getCarpeta().setFolio("1");

                given(documentoRepository.findById(documento.getId()))
                                .willReturn(Optional.of(documento));
                given(anexoRepository.findNombresAnexosByDocumentoId(documento.getId()))
                                .willReturn(Arrays.asList("Anexo1", "Anexo2"));

                DocumentoData documentoData = new DocumentoData();
                documentoData.setExhortoObservaciones("Observaciones");
                documentoData.setExhortoProcedencia("Procedencia");
                documento.setData(documentoData);

                Carpeta carpeta = new Carpeta();
                TipoJuicio tipoJuicioExhorto = new TipoJuicio();
                tipoJuicioExhorto.setNombre("Juicio Tipo");
                carpeta.setTipoJuicio(tipoJuicioExhorto);
                documento.setCarpeta(carpeta);

                ExhortoResponseRecord result = documentoService.getExhortoById(documento.getId());

                assertNotNull(result);
                assertEquals(Arrays.asList("Anexo1", "Anexo2"), result.anexos());
                assertEquals("Observaciones", result.exhortoObservaciones());
                assertEquals("Procedencia", result.exhortoProcedencia());
                assertEquals("Juicio Tipo", result.tipoJuicio());
        }

        @Test
        void getInfoPromocion() {
                List<String> anexos = List.of("Anexo1", "Anexo2");
                Documento documento = DocumentoSetUp.create(tipoJuicio);
                CarpetaResponseRecord carpetaResponseRecord = new CarpetaResponseRecord(
                                1, "actor 1", "demandado 1", null, null, null, null, null);

                DocumentoData documentoData = new DocumentoData().setTipoPromocion(TipoPromocion.ESCRITO);
                documento.setData(documentoData);

                given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));
                given(carpetaService.getCarpetaResponseByNumExpYearJuzgado(any(), any()))
                                .willReturn(carpetaResponseRecord);
                given(anexoRepository.findNombresAnexosByDocumentoId(anyInt())).willReturn(anexos);

                DocPromocionInfoRecord response = documentoService.getInfoPromocion(1);

                assertThat(response).isNotNull();
                assertEquals("000001", response.expediente());
                assertEquals(2024, response.year());
                assertEquals("JuzgadoTEST", response.juzgado());
                assertEquals("actor 1", response.actor());
                assertEquals("demandado 1", response.demandado());
                assertEquals("ESCRITO", response.tipoPromocion());
                assertEquals("Anexo1", response.anexos().get(0));
        }

        @Test
        void createExhortoSalida() {
                Documento exhortoSalida = DocumentoSetUp.create(tipoJuicio);
                exhortoSalida.setFolio("123")
                                .setTipoDocumento(TipoDocumento.EXHORTO_SALIDA);

                Persona persona = PersonaSetUp.createPersona();

                MockMultipartFile multipartFile = new MockMultipartFile(
                                "file",
                                "archivo.txt",
                                "text/plain",
                                "Contenido del archivo".getBytes(StandardCharsets.UTF_8));

                DocumentoExhortoSalidaRecord documentoExhortoSalidaRecord = new DocumentoExhortoSalidaRecord(
                                1,
                                "Juzgado 1",
                                "Nombre del exhorto",
                                "Sin observaciones",
                                LocalDate.now(),
                                LocalDate.now());

                given(carpetaRepository.findById(anyInt())).willReturn(Optional.of(exhortoSalida.getCarpeta()));
                given(personaService.getAuditor()).willReturn(persona);
                given(documentoRepository.save(any())).willReturn(exhortoSalida);
                given(documentoRepository.getNextValExhortoSalida()).willReturn(123L);

                DocumentoPromocionResponseRecord response = documentoService
                                .createExhortoSalida(documentoExhortoSalidaRecord, multipartFile);
                assertThat(response)
                                .isOfAnyClassIn(DocumentoPromocionResponseRecord.class)
                                .hasFieldOrPropertyWithValue("id", exhortoSalida.getId())
                                .hasFieldOrPropertyWithValue("folio", "123")
                                .hasFieldOrPropertyWithValue("tipoDocumento", TipoDocumento.EXHORTO_SALIDA);
        }

        @Test
        void testAdjuntarPromocion() {
                Documento documento = DocumentoSetUp.create(tipoJuicio);

                documento.setId(10).setEstatus(EstadoCarpeta.ASIGNADO).setTipoDocumento(TipoDocumento.PROMOCION)
                                .setFolio("0");

                given(documentoRepository.findById(any())).willReturn(Optional.of(documento));
                given(documentoRepository.save(any())).willReturn(documento);

                DocumentoPromocionResponseRecord responseRecord = documentoService.adjuntarPromocion(10);

                assertThat(responseRecord).isNotNull()
                                .hasFieldOrPropertyWithValue("id", documento.getId())
                                .hasFieldOrPropertyWithValue("folio", documento.getFolio())
                                .hasFieldOrPropertyWithValue("tipoDocumento", documento.getTipoDocumento());
        }

        @Test
        void saveSentenciaPublica() {
                Documento docSentencia = DocumentoSetUp.create(tipoJuicio);
                docSentencia.setTipoDocumento(TipoDocumento.SENTENCIA);

                Documento docSentenciaSave = DocumentoSetUp.create(tipoJuicio);
                docSentenciaSave.setTipoDocumento(TipoDocumento.SENTENCIA_PUBLICA);

                Persona persona = PersonaSetUp.createPersona();

                MockMultipartFile multipartFile = new MockMultipartFile(
                                "file",
                                "archivo.txt",
                                "text/plain",
                                "Contenido del archivo".getBytes(StandardCharsets.UTF_8));

                given(personaService.getAuditor()).willReturn(persona);
                given(documentoRepository.findById(anyInt())).willReturn(Optional.of(docSentencia));
                given(documentoRepository.save(any())).willReturn(docSentenciaSave);

                documentoService.saveSentenciaPublica(1, multipartFile);
                verify(personaService).getAuditor();
                verify(documentoRepository).findById(1);
                verify(documentoRepository).save(any(Documento.class));
                verify(digitalizacionService).guardarArchivo(multipartFile, docSentenciaSave.getId());
        }

        @Test
        void getAmparoById_success() {
                Integer documentoId = 1;

                DocumentoData documentoData = new DocumentoData();
                documentoData.setAmparoTipo("AD");
                documentoData.setAmparoFechaPresentacion(LocalDate.of(2024, 12, 1));
                documentoData.setAmparoFechaTermino(LocalDate.of(2024, 12, 15));
                documentoData.setAmparoImpugnacion(2);
                documentoData.setAmparoSentido("SOBRESEE");
                documentoData.setAmparoSentidoImpugnacion("REVOCA");
                documentoData.setAmparoQuejoso("Juan Pérez");
                documentoData.setAmparoTribunalId(5);
                documentoData.setAmparoSalaId(10);

                Carpeta carpeta = new Carpeta();
                carpeta.setId(100);

                Documento documento = new Documento();
                documento.setId(documentoId);
                documento.setData(documentoData);

                given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));
                given(carpetaRepository.findById(documentoId)).willReturn(Optional.of(carpeta));

                AmparoGetRecord response = documentoService.getAmparoById(documentoId);

                assertThat(response)
                                .isNotNull()
                                .hasFieldOrPropertyWithValue("carpetaId", carpeta.getId())
                                .hasFieldOrPropertyWithValue("tipoAmparo", documentoData.getAmparoTipo())
                                .hasFieldOrPropertyWithValue("fechaPresentacion",
                                                documentoData.getAmparoFechaPresentacion())
                                .hasFieldOrPropertyWithValue("fechaTermino", documentoData.getAmparoFechaTermino())
                                .hasFieldOrPropertyWithValue("impugnacion", documentoData.getAmparoImpugnacion())
                                .hasFieldOrPropertyWithValue("sentidoAmparo", documentoData.getAmparoSentido())
                                .hasFieldOrPropertyWithValue("impugnacionAmparo",
                                                documentoData.getAmparoSentidoImpugnacion())
                                .hasFieldOrPropertyWithValue("quejoso", documentoData.getAmparoQuejoso())
                                .hasFieldOrPropertyWithValue("tribunalId", documentoData.getAmparoTribunalId())
                                .hasFieldOrPropertyWithValue("salaId", documentoData.getAmparoSalaId());
        }

        @Test
        void updateAmparoData_success() {
                Integer documentoId = 1;
                AmparoUpdateRecord updateRecord = new AmparoUpdateRecord(
                                LocalDate.of(2024, 12, 1),
                                LocalDate.of(2024, 12, 15),
                                3,
                                "Favorable",
                                "Desfavorable",
                                "Juan Pérez",
                                5,
                                10,
                                "Amparo Directo");

                DocumentoData documentoData = new DocumentoData();
                documentoData.setAmparoFechaPresentacion(LocalDate.of(2024, 11, 1));
                documentoData.setAmparoFechaTermino(LocalDate.of(2024, 11, 15));
                documentoData.setAmparoImpugnacion(1);
                documentoData.setAmparoSentido("Neutral");
                documentoData.setAmparoSentidoImpugnacion("Neutral");
                documentoData.setAmparoQuejoso("Pedro López");
                documentoData.setAmparoTribunalId(2);
                documentoData.setAmparoSalaId(3);
                documentoData.setAmparoTipo("Amparo Indirecto");

                Documento documento = new Documento();
                documento.setId(documentoId);
                documento.setData(documentoData);

                given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));
                given(documentoRepository.save(any())).willReturn(documento);

                documentoService.updateAmparoData(documentoId, updateRecord);

                verify(documentoRepository).findById(documentoId);
                verify(documentoRepository).save(documento);

                assertThat(documento.getData())
                                .hasFieldOrPropertyWithValue("amparoFechaPresentacion",
                                                updateRecord.fechaPresentacion())
                                .hasFieldOrPropertyWithValue("amparoFechaTermino", updateRecord.fechaTermino())
                                .hasFieldOrPropertyWithValue("amparoImpugnacion", updateRecord.impugnacion())
                                .hasFieldOrPropertyWithValue("amparoSentido", updateRecord.sentidoAmparo())
                                .hasFieldOrPropertyWithValue("amparoSentidoImpugnacion",
                                                updateRecord.sentidoImpugnacion())
                                .hasFieldOrPropertyWithValue("amparoQuejoso", updateRecord.quejoso())
                                .hasFieldOrPropertyWithValue("amparoTribunalId", updateRecord.tribunalId())
                                .hasFieldOrPropertyWithValue("amparoSalaId", updateRecord.salaId())
                                .hasFieldOrPropertyWithValue("amparoTipo", updateRecord.tipoAmparo());
        }

        @Test
        void generateNumExpedientePenal_CJP() {
                Sede sede = new Sede().setId(1).setDistrito(
                                new Distrito().setId(1).setRegion("Centro-Poniente"));
                juzgadoFolios.getJuzgado().setNomenclatura("PUEBLA");
                juzgadoFolios.getJuzgado().setSede(sede);

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);

                String numExpediente = documentoService.generateNumExpedientePenal(
                                TipoCausa.CONTROL_JUDICIAL_PREVIO, juzgado, TipoCarpeta.DEMANDA);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]/CJP/PUEBLA");
        }

        @Test
        void generateNumExpedientePenal_CAI() {
                Sede sede = new Sede().setId(1).setDistrito(
                                new Distrito().setId(1).setRegion("Centro-Poniente"));
                juzgadoFolios.getJuzgado().setNomenclatura("PUEBLA");
                juzgadoFolios.getJuzgado().setSede(sede);

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);

                String numExpediente = documentoService.generateNumExpedientePenal(
                                TipoCausa.CONTROL_ACTOS_INVESTIGACION, juzgado, TipoCarpeta.DEMANDA);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]/CAI/PUEBLA");
        }

        @Test
        void generateNumExpedientePenal_EXT() {
                Sede sede = new Sede().setId(1).setDistrito(
                                new Distrito().setId(1).setRegion("Centro-Poniente"));
                juzgadoFolios.getJuzgado().setNomenclatura("PUEBLA");
                juzgadoFolios.getJuzgado().setSede(sede);

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);

                String numExpediente = documentoService.generateNumExpedientePenal(
                                TipoCausa.EXHORTO, juzgado, TipoCarpeta.DEMANDA);
                assertThat(numExpediente).containsPattern("EXT/[0-9]{6}/202[0-9]/PUEBLA");
        }

        @Test
        void generateNumExpedientePenal_JO() {
                Sede sede = new Sede().setId(1).setDistrito(
                                new Distrito().setId(1).setRegion("Centro-Poniente"));
                juzgadoFolios.getJuzgado().setNomenclatura("PUEBLA");
                juzgadoFolios.getJuzgado().setSede(sede);

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);

                String numExpediente = documentoService.generateNumExpedientePenal(
                                TipoCausa.JUICIO_ORAL, juzgado, TipoCarpeta.DEMANDA);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]/JO/CENTRO-PONIENTE");
        }

        @Test
        void generateNumExpedientePenal_EJE() {
                Sede sede = new Sede().setId(1).setDistrito(
                                new Distrito().setId(1).setRegion("Centro-Poniente"));
                juzgadoFolios.getJuzgado().setNomenclatura("PUEBLA");
                juzgadoFolios.getJuzgado().setSede(sede);

                given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
                given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);

                String numExpediente = documentoService.generateNumExpedientePenal(
                                TipoCausa.EJECUCION, juzgado, TipoCarpeta.DEMANDA);
                assertThat(numExpediente).containsPattern("[0-9]{6}/202[0-9]/EJE/PUEBLA");
        }
}
