package mx.gob.pjpuebla.trials.litigante;

import mx.gob.pjpuebla.migracion.readers.usuario.UsuarioMigracionRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.utils.audit.SetupServiceTest;
import mx.gob.pjpuebla.trials.litigante.responselitigante.AcuerdoSentenciaRecord;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia.AsistenciaAudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasExpedienteRecord;
import mx.gob.pjpuebla.trials.litigante.responsepromociones.PromocionesLitiganteRecord;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.Notificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LitiganteServiceTest extends SetupServiceTest {

    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;
    @Mock
    NotificacionesDetallesRepository notificacionesDetallesRepository;
    @Mock
    AsistenciaAudienciaRepository asistenciaAudienciaRepository;
    @Mock
    NotificacionRepository notificacionRepository;
    @Mock
    DocumentoRepository documentoRepository;
    @Mock
    UsuarioMigracionRepository usuarioMigracionRepository;
    @InjectMocks
    LitiganteService litiganteService;

    @Test
    void getExpedientesRelacionados_() {
        LitiganteExpedientesRecord litiganteExpedientesRecord = new LitiganteExpedientesRecord(
                100, "000001/2025", "MERCANTIL", "Mercantil (Tradicional)",
                "", "", "Juzgado 5 Mercantil TEST", 0L, "");
        given(personaDocumentoRepository.findByUsername(any(), any(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Arrays.asList(litiganteExpedientesRecord), PageRequest.of(0, 1), 1));
        given(personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(100, "Actor"))
                .willReturn(Arrays.asList("Julio Arenas", "Jorge Dominguez"));
        given(personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(100, "Demandado"))
                .willReturn(Arrays.asList("Romina Cervantes"));
        given(notificacionesDetallesRepository.countNotificacionesPorLeer(any(), any()))
                .willReturn(3L);
        Page<LitiganteExpedientesRecord> page = litiganteService.getExpedientesRelacionados("",PageRequest.of(1, 20));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("numeroExpediente", "000001/2025")
                .hasFieldOrPropertyWithValue("actorPrincipal", "Julio Arenas, Jorge Dominguez")
                .hasFieldOrPropertyWithValue("notificacionesPendientes", 3L);
    }

    @Test
    void getAcuerdosSentencias() {
        NotificacionesDetalles notificacionesDetalles = new NotificacionesDetalles()
                .setId(1)
                .setNotificacion(new Notificacion().setEstadoNotificacion(EstadoNotificacion.COMPLETADO)
                        .setFechaNotificado(LocalDateTime.now()).setId(1).setDocumento(
                        new Documento().setId(1).setCarpeta(
                                new Carpeta().setId(1).setExpediente("000001/2025").setJuzgado(
                                        new Juzgado().setNombre("Juzgado Mercantil")
                                )
                        )
                ));

        given(notificacionesDetallesRepository.getAllByUsername(any(), any(), any()))
                .willReturn(new PageImpl<>(Arrays.asList(notificacionesDetalles), PageRequest.of(0, 1), 1));

        Page<AcuerdoSentenciaRecord> result = litiganteService.getAcuerdosSentencias(PageRequest.of(0, 10));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        AcuerdoSentenciaRecord acuerdoSentenciaResponse= result.getContent().get(0);
        assertThat(acuerdoSentenciaResponse.numeroExpediente()).isEqualTo("000001/2025");
        assertThat(acuerdoSentenciaResponse.documentoId()).isEqualTo(1);
        assertThat(acuerdoSentenciaResponse.fechaNotificacion()).isEqualTo(notificacionesDetalles.getNotificacion().getFechaNotificado());
    }

    @Test
    void getExpedientesAudienciasRelacionados() {
        LitiganteExpedienteAudienciaRecord audienciaRecord = new LitiganteExpedienteAudienciaRecord(
                100, "000001/2025", "MERCANTIL", "Mercantil (Tradicional)",
                "Juzgado 5 Mercantil TEST", 100,
                LocalDateTime.parse("2025-01-13T08:00:00"), LocalDateTime.parse("2025-01-13T08:30:00")
        );

        given(asistenciaAudienciaRepository.getAllAudicenciasByUser(any(), any(PageRequest.class)))
                .willReturn(List.of(audienciaRecord));

        Page<LitiganteExpedienteListAudienciasRecord> page = litiganteService.getExpedientesAudienciasRelacionados(PageRequest.of(0, 10));

        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("numeroExpediente", "000001/2025")
                .hasFieldOrPropertyWithValue("audiencias", List.of(new AudienciasExpedienteRecord(
                        100, "2025-01-13", "08:00", "2025-01-13", "08:30"
                )));
    }


    @Test
    void getPromocionesLitigante() {
        Documento documento = new Documento();
        documento.setFolio("12345");
        documento.setRuta("ruta/documento");

        Carpeta carpeta = new Carpeta();
        carpeta.setExpediente("000001/2025");
        carpeta.setJuzgado(JuzgadoSetUp.createJuzgado());
        documento.setCarpeta(carpeta);

        Persona persona = new Persona();
        persona.setCorreoElectronico("correo@dominio.com");
        documento.setPersona(persona);

        Audit audit = new Audit();
        audit.setFechaAlta(LocalDateTime.now());
        documento.setAudit(audit);

        Page<Documento> docPage = new PageImpl<>(Collections.singletonList(documento));

        given(documentoRepository.findPromocionesLitigante(any(), any(), any(PageRequest.class))).willReturn(docPage);

        Page<PromocionesLitiganteRecord> promociones = litiganteService.getPromocionesLitigante("", PageRequest.of(0, 10));

        assertThat(promociones).isNotNull();
        assertThat(promociones.getContent()).hasSize(1);

        PromocionesLitiganteRecord promocionElectronica = promociones.getContent().get(0);
        assertThat(promocionElectronica.numeroPromocionE()).isEqualTo("12345");
        assertThat(promocionElectronica.usuarioOrigen()).isEqualTo("correo@dominio.com");
        assertThat(promocionElectronica.rutaArchivo()).isEqualTo("/opt/pjp/files/2025/JuzgadoTEST/000001/ruta/documento");
    }

}
