package mx.gob.pjpuebla.trials.workflow.reasignacionExpediente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReactivacionExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.reasignacionExpediente.records.ReasignacionExpedienteResponseRecord;

public class ReasignacionExpedienteServiceTest {

        @Mock
        private CarpetaRepository carpetaRepository;

        @Mock
        private PersonaService personaService;

        @Mock
        private TipoJuicioRepository tipoJuicioRepository;

        @Mock
        private JuzgadoRepository juzgadoRepository;

        @Mock
        private JuzgadoService juzgadoService;

        @Mock
        private DocumentoService documentoService;

        @Mock
        private DocumentoRepository documentoRepository;

        @Mock
        private MovimientoService movimientoService;

        @Mock
        private PersonaDocumentoRepository personaDocumentoRepository;

        @Mock
        private AnexoRepository anexoRepository;

        @InjectMocks
        private ReasignacionExpedienteService reasignacionExpedienteService;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testReasignarExpediente_Success() {
                // Arrange
                Integer carpetaParentId = 1;
                Persona auditor = new Persona();
                auditor.setOficialia(new Oficialia());
                Carpeta carpetaParent = new Carpeta();
                carpetaParent.setTipoJuicio(new TipoJuicio());
                Documento documentoParent = new Documento();
                documentoParent.setData(new DocumentoData());

                when(personaService.getAuditor()).thenReturn(auditor);
                when(carpetaRepository.findById(carpetaParentId)).thenReturn(Optional.of(carpetaParent));
                when(documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaParentId)).thenReturn(documentoParent);
                when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(new TipoJuicio()));
                when(juzgadoRepository.findJuzgadoByOficialiaId(any())).thenReturn(List.of(JuzgadoSetUp.createJuzgado().setTipoJuicios(List.of(TipoJuicioSetUp.createTipoJuicio()))));
                when(juzgadoService.getJuzgado(any(), any(), any())).thenReturn(new Juzgado());
                when(documentoService.getFolio(any())).thenReturn("Folio");
                when(documentoService.generateNumExpediente(any(), any())).thenReturn("Expediente");
                when(carpetaRepository.save(any())).thenReturn(new Carpeta());
                when(documentoRepository.save(any())).thenReturn(new Documento());
                when(personaDocumentoRepository.findByCarpetaId(any())).thenReturn(List.of(new PersonaDocumento()));
                when(anexoRepository.findAnexosByCarpetaIdOrDocumentoId(any(), any()))
                    .thenReturn(List.of(new AnexoBandejaRecepcionRecord(1, "anexo 1", EstadoAnexo.NORECIBIDO)));

                // Act
                ReasignacionExpedienteResponseRecord response = reasignacionExpedienteService.reasignarExpediente(carpetaParentId);

                // Assert
                assertEquals("Carpeta reasignada exitosamente", response.response());
        }

        @Test
        public void testReasignarExpediente_OficialiaNotFound() {
                // Arrange
                Integer carpetaParentId = 1;
                Persona auditor = new Persona();

                when(personaService.getAuditor()).thenReturn(auditor);

                // Act & Assert
                NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                        reasignacionExpedienteService.reasignarExpediente(carpetaParentId);
                });

                assertEquals("La persona no está relacionada con ninguna oficialía", exception.getBody().getDetail());
        }

        @Test
        public void testReasignarExpediente_CarpetaParentNotFound() {
                // Arrange
                Integer carpetaParentId = 1;
                Persona auditor = new Persona();
                auditor.setOficialia(new Oficialia());

                when(personaService.getAuditor()).thenReturn(auditor);
                when(carpetaRepository.findById(carpetaParentId)).thenReturn(Optional.empty());

                // Act & Assert
                NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                        reasignacionExpedienteService.reasignarExpediente(carpetaParentId);
                });

                assertEquals("Carpeta parent no encontrada", exception.getBody().getDetail());
        }

        @Test
        public void testReasignarExpediente_ExpedienteAlreadyReassigned() {
                // Arrange
                Integer carpetaParentId = 1;
                Persona auditor = new Persona();
                auditor.setOficialia(new Oficialia());
                Carpeta carpetaParent = new Carpeta();
                carpetaParent.setTipoJuicio(new TipoJuicio());
                Documento documentoParent = new Documento();
                DocumentoData dataDocumentParent = new DocumentoData();
                dataDocumentParent.setExpedienteReasignado(true);
                documentoParent.setData(dataDocumentParent);

                when(personaService.getAuditor()).thenReturn(auditor);
                when(carpetaRepository.findById(carpetaParentId)).thenReturn(Optional.of(carpetaParent));
                when(documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaParentId)).thenReturn(documentoParent);

                // Act & Assert
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                        reasignacionExpedienteService.reasignarExpediente(carpetaParentId);
                });

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                assertEquals("El expediente ya ha sido reasignado.", exception.getReason());
        }

        @Test
        public void testReactivacionExpediente_Success() {
                // Arrange
                Integer carpetaId = 1;
                Persona persona = new Persona();
                Carpeta carpeta = new Carpeta();
                carpeta.setEstatus(EstadoCarpeta.ARCHIVO_JUDICIAL);
                Juzgado juzgado = new Juzgado();
                juzgado.setEstado(Estado.ACTIVE);
                carpeta.setJuzgado(juzgado);
                Documento documento = new Documento();
                documento.setData(new DocumentoData());

                when(personaService.getAuditor()).thenReturn(persona);
                when(carpetaRepository.findById(carpetaId)).thenReturn(Optional.of(carpeta));
                when(documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaId)).thenReturn(documento);

                // Act
                ReactivacionExpedienteRecord response = reasignacionExpedienteService.reactivacionExpediente(carpetaId);

                // Assert
                assertEquals("Se ha reactivado el expediente nuevamente.", response.response());
        }

        @Test
        public void testReactivacionExpediente_ExpedienteAlreadyReassigned() {
                // Arrange
                Integer carpetaId = 1;
                Persona persona = new Persona();
                Carpeta carpeta = new Carpeta();
                carpeta.setEstatus(EstadoCarpeta.ARCHIVO_JUDICIAL);
                Juzgado juzgado = new Juzgado();
                juzgado.setEstado(Estado.ACTIVE);
                carpeta.setJuzgado(juzgado);
                Documento documento = new Documento();
                DocumentoData dataDocumentParent = new DocumentoData();
                dataDocumentParent.setExpedienteReasignado(true);
                documento.setData(dataDocumentParent);

                when(personaService.getAuditor()).thenReturn(persona);
                when(carpetaRepository.findById(carpetaId)).thenReturn(Optional.of(carpeta));
                when(documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaId)).thenReturn(documento);

                // Act & Assert
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                        reasignacionExpedienteService.reactivacionExpediente(carpetaId);
                });

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                assertEquals("No es posible reactivar el expediente ya que ha sido reasignado.", exception.getReason());
        }

        @Test
        public void testReactivacionExpediente_CarpetaNotInArchivoJudicial() {
                // Arrange
                Integer carpetaId = 1;
                Persona persona = new Persona();
                Carpeta carpeta = new Carpeta();
                carpeta.setEstatus(EstadoCarpeta.CAPTURA);
                Juzgado juzgado = new Juzgado();
                juzgado.setEstado(Estado.ACTIVE);
                carpeta.setJuzgado(juzgado);
                Documento documento = new Documento();
                documento.setData(new DocumentoData());

                when(personaService.getAuditor()).thenReturn(persona);
                when(carpetaRepository.findById(carpetaId)).thenReturn(Optional.of(carpeta));
                when(documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaId)).thenReturn(documento);

                // Act & Assert
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                        reasignacionExpedienteService.reactivacionExpediente(carpetaId);
                });

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                assertEquals("No es posible reactivar el expediente si no se encuentra en archivo judicial.", exception.getReason());
        }

        @Test
        public void testReactivacionExpediente_JuzgadoNotActive() {
                // Arrange
                Integer carpetaId = 1;
                Persona persona = new Persona();
                Carpeta carpeta = new Carpeta();
                carpeta.setEstatus(EstadoCarpeta.ARCHIVO_JUDICIAL);
                Juzgado juzgado = new Juzgado();
                juzgado.setEstado(Estado.INACTIVE);
                carpeta.setJuzgado(juzgado);
                Documento documento = new Documento();
                documento.setData(new DocumentoData());

                when(personaService.getAuditor()).thenReturn(persona);
                when(carpetaRepository.findById(carpetaId)).thenReturn(Optional.of(carpeta));
                when(documentoRepository.findByCarpetaIdAndTipoDocumentoIsNull(carpetaId)).thenReturn(documento);

                // Act & Assert
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                        reasignacionExpedienteService.reactivacionExpediente(carpetaId);
                });

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                assertEquals("No es posible reactivar el expediente ya que el juzgado no se encuentra activo.", exception.getReason());
        }
}