package mx.gob.pjpuebla.trials.litigante;

import mx.gob.pjpuebla.trials.core.utils.audit.SetupServiceTest;
import mx.gob.pjpuebla.trials.litigante.responselitigante.AcuerdoSentenciaRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.DocumentoExpedienteRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.ExpedienteAutorizadoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
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
    NotificacionRepository notificacionRepository;
    @InjectMocks
    LitiganteService litiganteService;

    @Test
    void getExpedientesRelacionados_() {
        LitiganteExpedientesRecord litiganteExpedientesRecord = new LitiganteExpedientesRecord(
                100, "000001/2025", "MERCANTIL", "Mercantil (Tradicional)",
                "", "", "Juzgado 5 Mercantil TEST", 0L);
        given(personaDocumentoRepository.findByUsername(any(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Arrays.asList(litiganteExpedientesRecord), PageRequest.of(0, 1), 1));
        given(personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(100, "Actor"))
                .willReturn(Arrays.asList("Julio Arenas", "Jorge Dominguez"));
        given(personaDocumentoRepository.findTipoPartePrincipalByCarpetaId(100, "Demandado"))
                .willReturn(Arrays.asList("Romina Cervantes"));
        given(notificacionesDetallesRepository.countNotificacionesPorLeer(any(), any()))
                .willReturn(3L);
        Page<LitiganteExpedientesRecord> page = litiganteService.getExpedientesRelacionados(PageRequest.of(1, 20));
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
                .setNotificacion(new Notificacion().setId(1).setDocumento(
                        new Documento().setId(1).setCarpeta(
                                new Carpeta().setId(1).setExpediente("000001/2025")
                        )
                ));

        given(notificacionesDetallesRepository.getAllByUsername(any(), any()))
                .willReturn(Collections.singletonList(notificacionesDetalles));

        ExpedienteAutorizadoRecord result = litiganteService.getAcuerdosSentencias();
        assertThat(result).isNotNull();
        assertThat(result.expedienteAutorizado()).hasSize(1);

        AcuerdoSentenciaRecord acuerdoSentenciaResponse= result.expedienteAutorizado().get(0);
        assertThat(acuerdoSentenciaResponse.documentoExpediente()).hasSize(1);
        assertThat(acuerdoSentenciaResponse.numeroExpediente()).isEqualTo("000001/2025");

        DocumentoExpedienteRecord documentoExpedienteResponse = acuerdoSentenciaResponse.documentoExpediente().get(0);
        assertThat(documentoExpedienteResponse.numeroAcuerdo()).isEqualTo(1);
        assertThat(documentoExpedienteResponse.fechaCompletado()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        assertThat(documentoExpedienteResponse.rutaArchivo()).isEqualTo("/api/litigante/documento/1");
    }
}
